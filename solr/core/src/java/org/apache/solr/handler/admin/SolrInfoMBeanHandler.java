begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|RequestHandlerBase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|XMLResponseParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrInfoMBean
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
operator|.
name|ErrorCode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|ContentStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|BinaryResponseWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|SolrQueryResponse
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_comment
comment|/**  * A request handler that provides info about all   * registered SolrInfoMBeans.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|SolrInfoMBeanHandler
specifier|public
class|class
name|SolrInfoMBeanHandler
extends|extends
name|RequestHandlerBase
block|{
comment|/**    * Take an array of any type and generate a Set containing the toString.    * Set is guarantee to never be null (but may be empty)    */
DECL|method|arrayToSet
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|arrayToSet
parameter_list|(
name|Object
index|[]
name|arr
parameter_list|)
block|{
name|HashSet
argument_list|<
name|String
argument_list|>
name|r
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|arr
condition|)
return|return
name|r
return|;
for|for
control|(
name|Object
name|o
range|:
name|arr
control|)
block|{
if|if
condition|(
literal|null
operator|!=
name|o
condition|)
name|r
operator|.
name|add
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|cats
init|=
name|getMBeanInfo
argument_list|(
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
literal|"diff"
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|ContentStream
name|body
init|=
literal|null
decl_stmt|;
try|try
block|{
name|body
operator|=
name|req
operator|.
name|getContentStreams
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"missing content-stream for diff"
argument_list|)
throw|;
block|}
name|String
name|content
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|body
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|ref
init|=
name|fromXML
argument_list|(
name|content
argument_list|)
decl_stmt|;
comment|// Normalize the output
name|SolrQueryResponse
name|wrap
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|wrap
operator|.
name|add
argument_list|(
literal|"solr-mbeans"
argument_list|,
name|cats
argument_list|)
expr_stmt|;
name|cats
operator|=
operator|(
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
operator|)
name|BinaryResponseWriter
operator|.
name|getParsedResponse
argument_list|(
name|req
argument_list|,
name|wrap
argument_list|)
operator|.
name|get
argument_list|(
literal|"solr-mbeans"
argument_list|)
expr_stmt|;
comment|// Get rid of irrelevant things
name|ref
operator|=
name|normalize
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|cats
operator|=
name|normalize
argument_list|(
name|cats
argument_list|)
expr_stmt|;
comment|// Only the changes
name|rsp
operator|.
name|add
argument_list|(
literal|"solr-mbeans"
argument_list|,
name|getDiff
argument_list|(
name|ref
argument_list|,
name|cats
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"solr-mbeans"
argument_list|,
name|cats
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|setHttpCaching
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// never cache, no matter what init config looks like
block|}
DECL|method|fromXML
specifier|static
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|fromXML
parameter_list|(
name|String
name|content
parameter_list|)
block|{
name|int
name|idx
init|=
name|content
operator|.
name|indexOf
argument_list|(
literal|"<response>"
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Body does not appear to be an XML response"
argument_list|)
throw|;
block|}
try|try
block|{
name|XMLResponseParser
name|parser
init|=
operator|new
name|XMLResponseParser
argument_list|()
decl_stmt|;
return|return
operator|(
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
operator|)
name|parser
operator|.
name|processResponse
argument_list|(
operator|new
name|StringReader
argument_list|(
name|content
operator|.
name|substring
argument_list|(
name|idx
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"solr-mbeans"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Unable to read original XML"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|method|getMBeanInfo
specifier|protected
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|getMBeanInfo
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|cats
init|=
operator|new
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|String
index|[]
name|requestedCats
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getParams
argument_list|(
literal|"cat"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|requestedCats
operator|||
literal|0
operator|==
name|requestedCats
operator|.
name|length
condition|)
block|{
for|for
control|(
name|SolrInfoMBean
operator|.
name|Category
name|cat
range|:
name|SolrInfoMBean
operator|.
name|Category
operator|.
name|values
argument_list|()
control|)
block|{
name|cats
operator|.
name|add
argument_list|(
name|cat
operator|.
name|name
argument_list|()
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|String
name|catName
range|:
name|requestedCats
control|)
block|{
name|cats
operator|.
name|add
argument_list|(
name|catName
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|requestedKeys
init|=
name|arrayToSet
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getParams
argument_list|(
literal|"key"
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|SolrInfoMBean
argument_list|>
name|reg
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getInfoRegistry
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|SolrInfoMBean
argument_list|>
name|entry
range|:
name|reg
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|SolrInfoMBean
name|m
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|requestedKeys
operator|.
name|isEmpty
argument_list|()
operator|||
name|requestedKeys
operator|.
name|contains
argument_list|(
name|key
argument_list|)
operator|)
condition|)
continue|continue;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|catInfo
init|=
name|cats
operator|.
name|get
argument_list|(
name|m
operator|.
name|getCategory
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|catInfo
condition|)
continue|continue;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|mBeanInfo
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|mBeanInfo
operator|.
name|add
argument_list|(
literal|"class"
argument_list|,
name|m
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|mBeanInfo
operator|.
name|add
argument_list|(
literal|"version"
argument_list|,
name|m
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|mBeanInfo
operator|.
name|add
argument_list|(
literal|"description"
argument_list|,
name|m
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|mBeanInfo
operator|.
name|add
argument_list|(
literal|"src"
argument_list|,
name|m
operator|.
name|getSource
argument_list|()
argument_list|)
expr_stmt|;
comment|// Use an external form
name|URL
index|[]
name|urls
init|=
name|m
operator|.
name|getDocs
argument_list|()
decl_stmt|;
if|if
condition|(
name|urls
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|urls
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|URL
name|url
range|:
name|urls
control|)
block|{
name|docs
operator|.
name|add
argument_list|(
name|url
operator|.
name|toExternalForm
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|mBeanInfo
operator|.
name|add
argument_list|(
literal|"docs"
argument_list|,
name|docs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getFieldBool
argument_list|(
name|key
argument_list|,
literal|"stats"
argument_list|,
literal|false
argument_list|)
condition|)
name|mBeanInfo
operator|.
name|add
argument_list|(
literal|"stats"
argument_list|,
name|m
operator|.
name|getStatistics
argument_list|()
argument_list|)
expr_stmt|;
name|catInfo
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|mBeanInfo
argument_list|)
expr_stmt|;
block|}
return|return
name|cats
return|;
block|}
DECL|method|getDiff
specifier|protected
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|getDiff
parameter_list|(
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|ref
parameter_list|,
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|now
parameter_list|)
block|{
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|changed
init|=
operator|new
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|// Cycle through each category
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ref
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|category
init|=
name|ref
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|ref_cat
init|=
name|ref
operator|.
name|get
argument_list|(
name|category
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|now_cat
init|=
name|now
operator|.
name|get
argument_list|(
name|category
argument_list|)
decl_stmt|;
if|if
condition|(
name|now_cat
operator|!=
literal|null
condition|)
block|{
name|String
name|ref_txt
init|=
name|ref_cat
operator|+
literal|""
decl_stmt|;
name|String
name|now_txt
init|=
name|now_cat
operator|+
literal|""
decl_stmt|;
if|if
condition|(
operator|!
name|ref_txt
operator|.
name|equals
argument_list|(
name|now_txt
argument_list|)
condition|)
block|{
comment|// Something in the category changed
comment|// Now iterate the real beans
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|cat
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|ref_cat
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|String
name|name
init|=
name|ref_cat
operator|.
name|getName
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|ref_bean
init|=
name|ref_cat
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|now_bean
init|=
name|now_cat
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|ref_txt
operator|=
name|ref_bean
operator|+
literal|""
expr_stmt|;
name|now_txt
operator|=
name|now_bean
operator|+
literal|""
expr_stmt|;
if|if
condition|(
operator|!
name|ref_txt
operator|.
name|equals
argument_list|(
name|now_txt
argument_list|)
condition|)
block|{
comment|//              System.out.println( "----" );
comment|//              System.out.println( category +" : " + name );
comment|//              System.out.println( "REF: " + ref_txt );
comment|//              System.out.println( "NOW: " + now_txt );
comment|// Calculate the differences
name|cat
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|diffNamedList
argument_list|(
name|ref_bean
argument_list|,
name|now_bean
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|cat
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|changed
operator|.
name|add
argument_list|(
name|category
argument_list|,
name|cat
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|changed
return|;
block|}
DECL|method|diffNamedList
specifier|public
name|NamedList
name|diffNamedList
parameter_list|(
name|NamedList
name|ref
parameter_list|,
name|NamedList
name|now
parameter_list|)
block|{
name|NamedList
name|out
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ref
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|ref
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Object
name|r
init|=
name|ref
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Object
name|n
init|=
name|now
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|add
argument_list|(
literal|"REMOVE "
operator|+
name|name
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|out
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|diffObject
argument_list|(
name|r
argument_list|,
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|now
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|now
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Object
name|v
init|=
name|now
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|add
argument_list|(
literal|"ADD "
operator|+
name|name
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|out
return|;
block|}
DECL|method|diffObject
specifier|public
name|Object
name|diffObject
parameter_list|(
name|Object
name|ref
parameter_list|,
name|Object
name|now
parameter_list|)
block|{
if|if
condition|(
name|ref
operator|instanceof
name|NamedList
condition|)
block|{
return|return
name|diffNamedList
argument_list|(
operator|(
name|NamedList
operator|)
name|ref
argument_list|,
operator|(
name|NamedList
operator|)
name|now
argument_list|)
return|;
block|}
if|if
condition|(
name|ref
operator|.
name|equals
argument_list|(
name|now
argument_list|)
condition|)
block|{
return|return
name|ref
return|;
block|}
name|StringBuilder
name|str
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|str
operator|.
name|append
argument_list|(
literal|"Was: "
argument_list|)
operator|.
name|append
argument_list|(
name|ref
argument_list|)
operator|.
name|append
argument_list|(
literal|", Now: "
argument_list|)
operator|.
name|append
argument_list|(
name|now
argument_list|)
expr_stmt|;
if|if
condition|(
name|ref
operator|instanceof
name|Number
condition|)
block|{
name|NumberFormat
name|nf
init|=
name|NumberFormat
operator|.
name|getIntegerInstance
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|ref
operator|instanceof
name|Double
operator|)
operator|||
operator|(
name|ref
operator|instanceof
name|Float
operator|)
condition|)
block|{
name|nf
operator|=
name|NumberFormat
operator|.
name|getInstance
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
expr_stmt|;
block|}
name|double
name|diff
init|=
operator|(
operator|(
name|Number
operator|)
name|now
operator|)
operator|.
name|doubleValue
argument_list|()
operator|-
operator|(
operator|(
name|Number
operator|)
name|ref
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|str
operator|.
name|append
argument_list|(
literal|", Delta: "
argument_list|)
operator|.
name|append
argument_list|(
name|nf
operator|.
name|format
argument_list|(
name|diff
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|str
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * The 'avgRequestsPerSecond' field will make everything look like it changed    */
DECL|method|normalize
specifier|public
name|NamedList
name|normalize
parameter_list|(
name|NamedList
name|input
parameter_list|)
block|{
name|input
operator|.
name|remove
argument_list|(
literal|"avgRequestsPerSecond"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|input
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|v
init|=
name|input
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|instanceof
name|NamedList
condition|)
block|{
name|input
operator|.
name|setVal
argument_list|(
name|i
argument_list|,
name|normalize
argument_list|(
operator|(
name|NamedList
operator|)
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|input
return|;
block|}
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Get Info (and statistics) for registered SolrInfoMBeans"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
block|}
end_class

end_unit

