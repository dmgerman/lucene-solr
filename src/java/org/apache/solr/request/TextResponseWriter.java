begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
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
name|schema
operator|.
name|IndexSchema
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
name|search
operator|.
name|DocList
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
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Set
import|;
end_import

begin_comment
comment|/** Base class for text-oriented response writers.  *  * @author yonik  * @version $Id$  */
end_comment

begin_class
DECL|class|TextResponseWriter
specifier|public
specifier|abstract
class|class
name|TextResponseWriter
block|{
DECL|field|writer
specifier|protected
specifier|final
name|Writer
name|writer
decl_stmt|;
DECL|field|schema
specifier|protected
specifier|final
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|req
specifier|protected
specifier|final
name|SolrQueryRequest
name|req
decl_stmt|;
DECL|field|rsp
specifier|protected
specifier|final
name|SolrQueryResponse
name|rsp
decl_stmt|;
comment|// the default set of fields to return for each document
DECL|field|returnFields
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|returnFields
decl_stmt|;
DECL|field|level
specifier|protected
name|int
name|level
decl_stmt|;
DECL|field|doIndent
specifier|protected
name|boolean
name|doIndent
decl_stmt|;
DECL|method|TextResponseWriter
specifier|public
name|TextResponseWriter
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|req
operator|.
name|getSchema
argument_list|()
expr_stmt|;
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
name|this
operator|.
name|rsp
operator|=
name|rsp
expr_stmt|;
name|String
name|indent
init|=
name|req
operator|.
name|getParam
argument_list|(
literal|"indent"
argument_list|)
decl_stmt|;
if|if
condition|(
name|indent
operator|!=
literal|null
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|indent
argument_list|)
operator|&&
operator|!
literal|"off"
operator|.
name|equals
argument_list|(
name|indent
argument_list|)
condition|)
block|{
name|doIndent
operator|=
literal|true
expr_stmt|;
block|}
name|returnFields
operator|=
name|rsp
operator|.
name|getReturnFields
argument_list|()
expr_stmt|;
block|}
comment|/** returns the Writer that the response is being written to */
DECL|method|getWriter
specifier|public
name|Writer
name|getWriter
parameter_list|()
block|{
return|return
name|writer
return|;
block|}
comment|// use a combination of tabs and spaces to minimize the size of an indented response.
DECL|field|indentArr
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|indentArr
init|=
operator|new
name|String
index|[]
block|{
literal|"\n"
block|,
literal|"\n "
block|,
literal|"\n  "
block|,
literal|"\n\t"
block|,
literal|"\n\t "
block|,
literal|"\n\t  "
block|,
comment|// could skip this one (the only 3 char seq)
literal|"\n\t\t"
block|,
literal|"\n\t\t "
block|}
decl_stmt|;
DECL|method|indent
specifier|public
name|void
name|indent
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|doIndent
condition|)
name|indent
argument_list|(
name|level
argument_list|)
expr_stmt|;
block|}
DECL|method|indent
specifier|public
name|void
name|indent
parameter_list|(
name|int
name|lev
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|arrsz
init|=
name|indentArr
operator|.
name|length
operator|-
literal|1
decl_stmt|;
comment|// power-of-two intent array (gratuitous optimization :-)
name|String
name|istr
init|=
name|indentArr
index|[
name|lev
operator|&
operator|(
name|indentArr
operator|.
name|length
operator|-
literal|1
operator|)
index|]
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|istr
argument_list|)
expr_stmt|;
block|}
comment|//
comment|// Functions to manipulate the current logical nesting level.
comment|// Any indentation will be partially based on level.
comment|//
DECL|method|setLevel
specifier|public
name|void
name|setLevel
parameter_list|(
name|int
name|level
parameter_list|)
block|{
name|this
operator|.
name|level
operator|=
name|level
expr_stmt|;
block|}
DECL|method|level
specifier|public
name|int
name|level
parameter_list|()
block|{
return|return
name|level
return|;
block|}
DECL|method|incLevel
specifier|public
name|int
name|incLevel
parameter_list|()
block|{
return|return
operator|++
name|level
return|;
block|}
DECL|method|decLevel
specifier|public
name|int
name|decLevel
parameter_list|()
block|{
return|return
operator|--
name|level
return|;
block|}
DECL|method|setIndent
specifier|public
name|void
name|setIndent
parameter_list|(
name|boolean
name|doIndent
parameter_list|)
block|{
name|this
operator|.
name|doIndent
operator|=
name|doIndent
expr_stmt|;
block|}
DECL|method|writeNamedList
specifier|public
specifier|abstract
name|void
name|writeNamedList
parameter_list|(
name|String
name|name
parameter_list|,
name|NamedList
name|val
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeVal
specifier|public
name|void
name|writeVal
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|val
parameter_list|)
throws|throws
name|IOException
block|{
comment|// if there get to be enough types, perhaps hashing on the type
comment|// to get a handler might be faster (but types must be exact to do that...)
comment|// go in order of most common to least common
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|writeNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|String
condition|)
block|{
name|writeStr
argument_list|(
name|name
argument_list|,
name|val
operator|.
name|toString
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// micro-optimization... using toString() avoids a cast first
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Integer
condition|)
block|{
name|writeInt
argument_list|(
name|name
argument_list|,
name|val
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Boolean
condition|)
block|{
name|writeBool
argument_list|(
name|name
argument_list|,
name|val
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Long
condition|)
block|{
name|writeLong
argument_list|(
name|name
argument_list|,
name|val
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Date
condition|)
block|{
name|writeDate
argument_list|(
name|name
argument_list|,
operator|(
name|Date
operator|)
name|val
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Float
condition|)
block|{
comment|// we pass the float instead of using toString() because
comment|// it may need special formatting. same for double.
name|writeFloat
argument_list|(
name|name
argument_list|,
operator|(
operator|(
name|Float
operator|)
name|val
operator|)
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Double
condition|)
block|{
name|writeDouble
argument_list|(
name|name
argument_list|,
operator|(
operator|(
name|Double
operator|)
name|val
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Document
condition|)
block|{
name|writeDoc
argument_list|(
name|name
argument_list|,
operator|(
name|Document
operator|)
name|val
argument_list|,
name|returnFields
argument_list|,
literal|0.0f
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|DocList
condition|)
block|{
comment|// requires access to IndexReader
name|writeDocList
argument_list|(
name|name
argument_list|,
operator|(
name|DocList
operator|)
name|val
argument_list|,
name|returnFields
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// }
comment|// else if (val instanceof DocSet) {
comment|// how do we know what fields to read?
comment|// todo: have a DocList/DocSet wrapper that
comment|// restricts the fields to write...?
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Map
condition|)
block|{
name|writeMap
argument_list|(
name|name
argument_list|,
operator|(
name|Map
operator|)
name|val
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|NamedList
condition|)
block|{
name|writeNamedList
argument_list|(
name|name
argument_list|,
operator|(
name|NamedList
operator|)
name|val
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Iterable
condition|)
block|{
name|writeArray
argument_list|(
name|name
argument_list|,
operator|(
operator|(
name|Iterable
operator|)
name|val
operator|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Object
index|[]
condition|)
block|{
name|writeArray
argument_list|(
name|name
argument_list|,
operator|(
name|Object
index|[]
operator|)
name|val
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Iterator
condition|)
block|{
name|writeArray
argument_list|(
name|name
argument_list|,
operator|(
name|Iterator
operator|)
name|val
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// default... for debugging only
name|writeStr
argument_list|(
name|name
argument_list|,
name|val
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|':'
operator|+
name|val
operator|.
name|toString
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|// names are passed when writing primitives like writeInt to allow many different
comment|// types of formats, including those where the name may come after the value (like
comment|// some XML formats).
DECL|method|writeDoc
specifier|public
specifier|abstract
name|void
name|writeDoc
parameter_list|(
name|String
name|name
parameter_list|,
name|Document
name|doc
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|returnFields
parameter_list|,
name|float
name|score
parameter_list|,
name|boolean
name|includeScore
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeDocList
specifier|public
specifier|abstract
name|void
name|writeDocList
parameter_list|(
name|String
name|name
parameter_list|,
name|DocList
name|ids
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|,
name|Map
name|otherFields
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeStr
specifier|public
specifier|abstract
name|void
name|writeStr
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|,
name|boolean
name|needsEscaping
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeMap
specifier|public
specifier|abstract
name|void
name|writeMap
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
name|val
parameter_list|,
name|boolean
name|excludeOuter
parameter_list|,
name|boolean
name|isFirstVal
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeArray
specifier|public
specifier|abstract
name|void
name|writeArray
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
index|[]
name|val
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeArray
specifier|public
specifier|abstract
name|void
name|writeArray
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterator
name|val
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeNull
specifier|public
specifier|abstract
name|void
name|writeNull
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** if this form of the method is called, val is the Java string form of an int */
DECL|method|writeInt
specifier|public
specifier|abstract
name|void
name|writeInt
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeInt
specifier|public
name|void
name|writeInt
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writeInt
argument_list|(
name|name
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** if this form of the method is called, val is the Java string form of a long */
DECL|method|writeLong
specifier|public
specifier|abstract
name|void
name|writeLong
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeLong
specifier|public
name|void
name|writeLong
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writeLong
argument_list|(
name|name
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** if this form of the method is called, val is the Java string form of a boolean */
DECL|method|writeBool
specifier|public
specifier|abstract
name|void
name|writeBool
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeBool
specifier|public
name|void
name|writeBool
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writeBool
argument_list|(
name|name
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** if this form of the method is called, val is the Java string form of a float */
DECL|method|writeFloat
specifier|public
specifier|abstract
name|void
name|writeFloat
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeFloat
specifier|public
name|void
name|writeFloat
parameter_list|(
name|String
name|name
parameter_list|,
name|float
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writeFloat
argument_list|(
name|name
argument_list|,
name|Float
operator|.
name|toString
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** if this form of the method is called, val is the Java string form of a double */
DECL|method|writeDouble
specifier|public
specifier|abstract
name|void
name|writeDouble
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeDouble
specifier|public
name|void
name|writeDouble
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writeDouble
argument_list|(
name|name
argument_list|,
name|Double
operator|.
name|toString
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|writeDate
specifier|public
specifier|abstract
name|void
name|writeDate
parameter_list|(
name|String
name|name
parameter_list|,
name|Date
name|val
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** if this form of the method is called, val is the Solr ISO8601 based date format */
DECL|method|writeDate
specifier|public
specifier|abstract
name|void
name|writeDate
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

