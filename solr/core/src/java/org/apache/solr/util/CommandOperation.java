begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Reader
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
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|Map
import|;
end_import

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
name|input
operator|.
name|CharSequenceReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|StrUtils
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
name|Utils
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
name|org
operator|.
name|noggit
operator|.
name|JSONParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|ObjectBuilder
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonMap
import|;
end_import

begin_import
import|import static
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
name|StrUtils
operator|.
name|formatString
import|;
end_import

begin_import
import|import static
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
name|Utils
operator|.
name|toJSON
import|;
end_import

begin_class
DECL|class|CommandOperation
specifier|public
class|class
name|CommandOperation
block|{
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|commandData
specifier|private
name|Object
name|commandData
decl_stmt|;
comment|//this is most often a map
DECL|field|errors
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|errors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|CommandOperation
specifier|public
name|CommandOperation
parameter_list|(
name|String
name|operationName
parameter_list|,
name|Object
name|metaData
parameter_list|)
block|{
name|commandData
operator|=
name|metaData
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|operationName
expr_stmt|;
block|}
DECL|method|getStr
specifier|public
name|String
name|getStr
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|def
parameter_list|)
block|{
if|if
condition|(
name|ROOT_OBJ
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|Object
name|obj
init|=
name|getRootPrimitive
argument_list|()
decl_stmt|;
return|return
name|obj
operator|==
name|def
condition|?
literal|null
else|:
name|String
operator|.
name|valueOf
argument_list|(
name|obj
argument_list|)
return|;
block|}
name|Object
name|o
init|=
name|getMapVal
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|o
operator|==
literal|null
condition|?
name|def
else|:
name|String
operator|.
name|valueOf
argument_list|(
name|o
argument_list|)
return|;
block|}
DECL|method|getBoolean
specifier|public
name|boolean
name|getBoolean
parameter_list|(
name|String
name|key
parameter_list|,
name|boolean
name|def
parameter_list|)
block|{
name|String
name|v
init|=
name|getStr
argument_list|(
name|key
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|v
operator|==
literal|null
condition|?
name|def
else|:
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|v
argument_list|)
return|;
block|}
DECL|method|setCommandData
specifier|public
name|void
name|setCommandData
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|commandData
operator|=
name|o
expr_stmt|;
block|}
DECL|method|getDataMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getDataMap
parameter_list|()
block|{
if|if
condition|(
name|commandData
operator|instanceof
name|Map
condition|)
block|{
comment|//noinspection unchecked
return|return
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|commandData
return|;
block|}
name|addError
argument_list|(
name|StrUtils
operator|.
name|formatString
argument_list|(
literal|"The command ''{0}'' should have the values as a json object {key:val} format"
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
DECL|method|getRootPrimitive
specifier|private
name|Object
name|getRootPrimitive
parameter_list|()
block|{
if|if
condition|(
name|commandData
operator|instanceof
name|Map
condition|)
block|{
name|errors
operator|.
name|add
argument_list|(
name|StrUtils
operator|.
name|formatString
argument_list|(
literal|"The value has to be a string for command : ''{0}'' "
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|commandData
return|;
block|}
DECL|method|getVal
specifier|public
name|Object
name|getVal
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|getMapVal
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|getMapVal
specifier|private
name|Object
name|getMapVal
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|commandData
operator|instanceof
name|Map
condition|)
block|{
name|Map
name|metaData
init|=
operator|(
name|Map
operator|)
name|commandData
decl_stmt|;
return|return
name|metaData
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
else|else
block|{
name|String
name|msg
init|=
literal|" value has to be an object for operation :"
operator|+
name|name
decl_stmt|;
if|if
condition|(
operator|!
name|errors
operator|.
name|contains
argument_list|(
name|msg
argument_list|)
condition|)
name|errors
operator|.
name|add
argument_list|(
name|msg
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|getStrs
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getStrs
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|val
init|=
name|getStrs
argument_list|(
name|key
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|errors
operator|.
name|add
argument_list|(
name|StrUtils
operator|.
name|formatString
argument_list|(
name|REQD
argument_list|,
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|val
return|;
block|}
DECL|method|unknownOperation
specifier|public
name|void
name|unknownOperation
parameter_list|()
block|{
name|addError
argument_list|(
name|formatString
argument_list|(
literal|"Unknown operation ''{0}'' "
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|REQD
specifier|static
specifier|final
name|String
name|REQD
init|=
literal|"''{0}'' is a required field"
decl_stmt|;
comment|/**    * Get collection of values for a key. If only one val is present a    * single value collection is returned    */
DECL|method|getStrs
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getStrs
parameter_list|(
name|String
name|key
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|def
parameter_list|)
block|{
name|Object
name|v
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|ROOT_OBJ
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|v
operator|=
name|getRootPrimitive
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|v
operator|=
name|getMapVal
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return
name|def
return|;
block|}
else|else
block|{
if|if
condition|(
name|v
operator|instanceof
name|List
condition|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
operator|(
name|List
operator|)
name|v
control|)
block|{
name|l
operator|.
name|add
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|o
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|l
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|def
return|;
return|return
name|l
return|;
block|}
else|else
block|{
return|return
name|singletonList
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
comment|/**    * Get a required field. If missing it adds to the errors    */
DECL|method|getStr
specifier|public
name|String
name|getStr
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|ROOT_OBJ
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|Object
name|obj
init|=
name|getRootPrimitive
argument_list|()
decl_stmt|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
name|errors
operator|.
name|add
argument_list|(
name|StrUtils
operator|.
name|formatString
argument_list|(
name|REQD
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|obj
operator|==
literal|null
condition|?
literal|null
else|:
name|String
operator|.
name|valueOf
argument_list|(
name|obj
argument_list|)
return|;
block|}
name|String
name|s
init|=
name|getStr
argument_list|(
name|key
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
name|errors
operator|.
name|add
argument_list|(
name|StrUtils
operator|.
name|formatString
argument_list|(
name|REQD
argument_list|,
name|key
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
DECL|method|errorDetails
specifier|private
name|Map
name|errorDetails
parameter_list|()
block|{
return|return
name|Utils
operator|.
name|makeMap
argument_list|(
name|name
argument_list|,
name|commandData
argument_list|,
name|ERR_MSGS
argument_list|,
name|errors
argument_list|)
return|;
block|}
DECL|method|hasError
specifier|public
name|boolean
name|hasError
parameter_list|()
block|{
return|return
operator|!
name|errors
operator|.
name|isEmpty
argument_list|()
return|;
block|}
DECL|method|addError
specifier|public
name|void
name|addError
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|errors
operator|.
name|contains
argument_list|(
name|s
argument_list|)
condition|)
return|return;
name|errors
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get all the values from the metadata for the command    * without the specified keys    */
DECL|method|getValuesExcluding
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getValuesExcluding
parameter_list|(
name|String
modifier|...
name|keys
parameter_list|)
block|{
name|getMapVal
argument_list|(
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasError
argument_list|()
condition|)
return|return
name|emptyMap
argument_list|()
return|;
comment|//just to verify the type is Map
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|cp
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|commandData
argument_list|)
decl_stmt|;
if|if
condition|(
name|keys
operator|==
literal|null
condition|)
return|return
name|cp
return|;
for|for
control|(
name|String
name|key
range|:
name|keys
control|)
block|{
name|cp
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
return|return
name|cp
return|;
block|}
DECL|method|getErrors
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getErrors
parameter_list|()
block|{
return|return
name|errors
return|;
block|}
DECL|field|ERR_MSGS
specifier|public
specifier|static
specifier|final
name|String
name|ERR_MSGS
init|=
literal|"errorMessages"
decl_stmt|;
DECL|field|ROOT_OBJ
specifier|public
specifier|static
specifier|final
name|String
name|ROOT_OBJ
init|=
literal|""
decl_stmt|;
DECL|method|captureErrors
specifier|public
specifier|static
name|List
argument_list|<
name|Map
argument_list|>
name|captureErrors
parameter_list|(
name|List
argument_list|<
name|CommandOperation
argument_list|>
name|ops
parameter_list|)
block|{
name|List
argument_list|<
name|Map
argument_list|>
name|errors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|CommandOperation
name|op
range|:
name|ops
control|)
block|{
if|if
condition|(
name|op
operator|.
name|hasError
argument_list|()
condition|)
block|{
name|errors
operator|.
name|add
argument_list|(
name|op
operator|.
name|errorDetails
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|errors
return|;
block|}
comment|/**    * Parse the command operations into command objects    */
DECL|method|parse
specifier|public
specifier|static
name|List
argument_list|<
name|CommandOperation
argument_list|>
name|parse
parameter_list|(
name|Reader
name|rdr
parameter_list|)
throws|throws
name|IOException
block|{
name|JSONParser
name|parser
init|=
operator|new
name|JSONParser
argument_list|(
name|rdr
argument_list|)
decl_stmt|;
name|ObjectBuilder
name|ob
init|=
operator|new
name|ObjectBuilder
argument_list|(
name|parser
argument_list|)
decl_stmt|;
if|if
condition|(
name|parser
operator|.
name|lastEvent
argument_list|()
operator|!=
name|JSONParser
operator|.
name|OBJECT_START
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"The JSON must be an Object of the form {\"command\": {...},..."
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|CommandOperation
argument_list|>
name|operations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|ev
init|=
name|parser
operator|.
name|nextEvent
argument_list|()
decl_stmt|;
if|if
condition|(
name|ev
operator|==
name|JSONParser
operator|.
name|OBJECT_END
condition|)
return|return
name|operations
return|;
name|Object
name|key
init|=
name|ob
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|ev
operator|=
name|parser
operator|.
name|nextEvent
argument_list|()
expr_stmt|;
name|Object
name|val
init|=
name|ob
operator|.
name|getVal
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|instanceof
name|List
condition|)
block|{
name|List
name|list
init|=
operator|(
name|List
operator|)
name|val
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|list
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|Map
operator|)
condition|)
block|{
name|operations
operator|.
name|add
argument_list|(
operator|new
name|CommandOperation
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|key
argument_list|)
argument_list|,
name|list
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
else|else
block|{
name|operations
operator|.
name|add
argument_list|(
operator|new
name|CommandOperation
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|key
argument_list|)
argument_list|,
name|o
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|operations
operator|.
name|add
argument_list|(
operator|new
name|CommandOperation
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|key
argument_list|)
argument_list|,
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getCopy
specifier|public
name|CommandOperation
name|getCopy
parameter_list|()
block|{
return|return
operator|new
name|CommandOperation
argument_list|(
name|name
argument_list|,
name|commandData
argument_list|)
return|;
block|}
DECL|method|getMap
specifier|public
name|Map
name|getMap
parameter_list|(
name|String
name|key
parameter_list|,
name|Map
name|def
parameter_list|)
block|{
name|Object
name|o
init|=
name|getMapVal
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
name|def
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|Map
operator|)
condition|)
block|{
name|addError
argument_list|(
name|StrUtils
operator|.
name|formatString
argument_list|(
literal|"''{0}'' must be a map"
argument_list|,
name|key
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|def
return|;
block|}
else|else
block|{
return|return
operator|(
name|Map
operator|)
name|o
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|String
argument_list|(
name|toJSON
argument_list|(
name|singletonMap
argument_list|(
name|name
argument_list|,
name|commandData
argument_list|)
argument_list|)
argument_list|,
name|IOUtils
operator|.
name|UTF_8
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
comment|//should not happen
return|return
literal|""
return|;
block|}
block|}
DECL|method|readCommands
specifier|public
specifier|static
name|List
argument_list|<
name|CommandOperation
argument_list|>
name|readCommands
parameter_list|(
name|Iterable
argument_list|<
name|ContentStream
argument_list|>
name|streams
parameter_list|,
name|SolrQueryResponse
name|resp
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|streams
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"missing content stream"
argument_list|)
throw|;
block|}
name|ArrayList
argument_list|<
name|CommandOperation
argument_list|>
name|ops
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ContentStream
name|stream
range|:
name|streams
control|)
name|ops
operator|.
name|addAll
argument_list|(
name|parse
argument_list|(
name|stream
operator|.
name|getReader
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Map
argument_list|>
name|errList
init|=
name|CommandOperation
operator|.
name|captureErrors
argument_list|(
name|ops
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|errList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|resp
operator|.
name|add
argument_list|(
name|CommandOperation
operator|.
name|ERR_MSGS
argument_list|,
name|errList
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|ops
return|;
block|}
DECL|method|clone
specifier|public
specifier|static
name|List
argument_list|<
name|CommandOperation
argument_list|>
name|clone
parameter_list|(
name|List
argument_list|<
name|CommandOperation
argument_list|>
name|ops
parameter_list|)
block|{
name|List
argument_list|<
name|CommandOperation
argument_list|>
name|opsCopy
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|ops
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|CommandOperation
name|op
range|:
name|ops
control|)
name|opsCopy
operator|.
name|add
argument_list|(
name|op
operator|.
name|getCopy
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|opsCopy
return|;
block|}
block|}
end_class

end_unit

