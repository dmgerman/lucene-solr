begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.beans
package|package
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
name|beans
package|;
end_package

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
name|SolrDocumentList
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
name|SolrDocument
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
name|SolrInputDocument
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_comment
comment|/**  * A class to map objects to and from solr documents.  *  *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|DocumentObjectBinder
specifier|public
class|class
name|DocumentObjectBinder
block|{
DECL|field|infocache
specifier|private
specifier|final
name|Map
argument_list|<
name|Class
argument_list|,
name|List
argument_list|<
name|DocField
argument_list|>
argument_list|>
name|infocache
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|DocumentObjectBinder
specifier|public
name|DocumentObjectBinder
parameter_list|()
block|{   }
DECL|method|getBeans
specifier|public
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|getBeans
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|,
name|SolrDocumentList
name|solrDocList
parameter_list|)
block|{
name|List
argument_list|<
name|DocField
argument_list|>
name|fields
init|=
name|getDocFields
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|T
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|solrDocList
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SolrDocument
name|sdoc
range|:
name|solrDocList
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|getBean
argument_list|(
name|clazz
argument_list|,
name|fields
argument_list|,
name|sdoc
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|getBean
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|getBean
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|,
name|SolrDocument
name|solrDoc
parameter_list|)
block|{
return|return
name|getBean
argument_list|(
name|clazz
argument_list|,
literal|null
argument_list|,
name|solrDoc
argument_list|)
return|;
block|}
DECL|method|getBean
specifier|private
parameter_list|<
name|T
parameter_list|>
name|T
name|getBean
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|,
name|List
argument_list|<
name|DocField
argument_list|>
name|fields
parameter_list|,
name|SolrDocument
name|solrDoc
parameter_list|)
block|{
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
name|fields
operator|=
name|getDocFields
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|T
name|obj
init|=
name|clazz
operator|.
name|newInstance
argument_list|()
decl_stmt|;
for|for
control|(
name|DocField
name|docField
range|:
name|fields
control|)
block|{
name|docField
operator|.
name|inject
argument_list|(
name|obj
argument_list|,
name|solrDoc
argument_list|)
expr_stmt|;
block|}
return|return
name|obj
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BindingException
argument_list|(
literal|"Could not instantiate object of "
operator|+
name|clazz
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|toSolrInputDocument
specifier|public
name|SolrInputDocument
name|toSolrInputDocument
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
name|List
argument_list|<
name|DocField
argument_list|>
name|fields
init|=
name|getDocFields
argument_list|(
name|obj
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|BindingException
argument_list|(
literal|"class: "
operator|+
name|obj
operator|.
name|getClass
argument_list|()
operator|+
literal|" does not define any fields."
argument_list|)
throw|;
block|}
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
for|for
control|(
name|DocField
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
name|field
operator|.
name|dynamicFieldNamePatternMatcher
operator|!=
literal|null
operator|&&
name|field
operator|.
name|get
argument_list|(
name|obj
argument_list|)
operator|!=
literal|null
operator|&&
name|field
operator|.
name|isContainedInMap
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|mapValue
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|field
operator|.
name|get
argument_list|(
name|obj
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|e
range|:
name|mapValue
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|doc
operator|.
name|setField
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|field
operator|.
name|child
operator|!=
literal|null
condition|)
block|{
name|addChild
argument_list|(
name|obj
argument_list|,
name|field
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|doc
operator|.
name|setField
argument_list|(
name|field
operator|.
name|name
argument_list|,
name|field
operator|.
name|get
argument_list|(
name|obj
argument_list|)
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|doc
return|;
block|}
DECL|method|addChild
specifier|private
name|void
name|addChild
parameter_list|(
name|Object
name|obj
parameter_list|,
name|DocField
name|field
parameter_list|,
name|SolrInputDocument
name|doc
parameter_list|)
block|{
name|Object
name|val
init|=
name|field
operator|.
name|get
argument_list|(
name|obj
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|val
operator|instanceof
name|Collection
condition|)
block|{
name|Collection
name|collection
init|=
operator|(
name|Collection
operator|)
name|val
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|collection
control|)
block|{
name|SolrInputDocument
name|child
init|=
name|toSolrInputDocument
argument_list|(
name|o
argument_list|)
decl_stmt|;
name|doc
operator|.
name|addChildDocument
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|val
operator|.
name|getClass
argument_list|()
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|Object
index|[]
name|objs
init|=
operator|(
name|Object
index|[]
operator|)
name|val
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|objs
control|)
name|doc
operator|.
name|addChildDocument
argument_list|(
name|toSolrInputDocument
argument_list|(
name|o
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|doc
operator|.
name|addChildDocument
argument_list|(
name|toSolrInputDocument
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getDocFields
specifier|private
name|List
argument_list|<
name|DocField
argument_list|>
name|getDocFields
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
name|List
argument_list|<
name|DocField
argument_list|>
name|fields
init|=
name|infocache
operator|.
name|get
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|infocache
init|)
block|{
name|infocache
operator|.
name|put
argument_list|(
name|clazz
argument_list|,
name|fields
operator|=
name|collectInfo
argument_list|(
name|clazz
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|fields
return|;
block|}
DECL|method|collectInfo
specifier|private
name|List
argument_list|<
name|DocField
argument_list|>
name|collectInfo
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
name|List
argument_list|<
name|DocField
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Class
name|superClazz
init|=
name|clazz
decl_stmt|;
name|List
argument_list|<
name|AccessibleObject
argument_list|>
name|members
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|superClazz
operator|!=
literal|null
operator|&&
name|superClazz
operator|!=
name|Object
operator|.
name|class
condition|)
block|{
name|members
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|superClazz
operator|.
name|getDeclaredFields
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|members
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|superClazz
operator|.
name|getDeclaredMethods
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|superClazz
operator|=
name|superClazz
operator|.
name|getSuperclass
argument_list|()
expr_stmt|;
block|}
name|boolean
name|childFieldFound
init|=
literal|false
decl_stmt|;
for|for
control|(
name|AccessibleObject
name|member
range|:
name|members
control|)
block|{
if|if
condition|(
name|member
operator|.
name|isAnnotationPresent
argument_list|(
name|Field
operator|.
name|class
argument_list|)
condition|)
block|{
name|member
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|DocField
name|df
init|=
operator|new
name|DocField
argument_list|(
name|member
argument_list|)
decl_stmt|;
if|if
condition|(
name|df
operator|.
name|child
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|childFieldFound
condition|)
throw|throw
operator|new
name|BindingException
argument_list|(
name|clazz
operator|.
name|getName
argument_list|()
operator|+
literal|" cannot have more than one Field with child=true"
argument_list|)
throw|;
name|childFieldFound
operator|=
literal|true
expr_stmt|;
block|}
name|fields
operator|.
name|add
argument_list|(
name|df
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|fields
return|;
block|}
DECL|class|DocField
specifier|private
class|class
name|DocField
block|{
DECL|field|annotation
specifier|private
name|Field
name|annotation
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|field
specifier|private
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
name|field
decl_stmt|;
DECL|field|setter
specifier|private
name|Method
name|setter
decl_stmt|;
DECL|field|getter
specifier|private
name|Method
name|getter
decl_stmt|;
DECL|field|type
specifier|private
name|Class
name|type
decl_stmt|;
DECL|field|isArray
specifier|private
name|boolean
name|isArray
decl_stmt|;
DECL|field|isList
specifier|private
name|boolean
name|isList
decl_stmt|;
DECL|field|child
specifier|private
name|List
argument_list|<
name|DocField
argument_list|>
name|child
decl_stmt|;
comment|/*      * dynamic fields may use a Map based data structure to bind a given field.      * if a mapping is done using, "Map<String, List<String>> foo",<code>isContainedInMap</code>      * is set to<code>TRUE</code> as well as<code>isList</code> is set to<code>TRUE</code>      */
DECL|field|isContainedInMap
specifier|private
name|boolean
name|isContainedInMap
decl_stmt|;
DECL|field|dynamicFieldNamePatternMatcher
specifier|private
name|Pattern
name|dynamicFieldNamePatternMatcher
decl_stmt|;
DECL|method|DocField
specifier|public
name|DocField
parameter_list|(
name|AccessibleObject
name|member
parameter_list|)
block|{
if|if
condition|(
name|member
operator|instanceof
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
condition|)
block|{
name|field
operator|=
operator|(
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
operator|)
name|member
expr_stmt|;
block|}
else|else
block|{
name|setter
operator|=
operator|(
name|Method
operator|)
name|member
expr_stmt|;
block|}
name|annotation
operator|=
name|member
operator|.
name|getAnnotation
argument_list|(
name|Field
operator|.
name|class
argument_list|)
expr_stmt|;
name|storeName
argument_list|(
name|annotation
argument_list|)
expr_stmt|;
name|storeType
argument_list|()
expr_stmt|;
comment|// Look for a matching getter
if|if
condition|(
name|setter
operator|!=
literal|null
condition|)
block|{
name|String
name|gname
init|=
name|setter
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|gname
operator|.
name|startsWith
argument_list|(
literal|"set"
argument_list|)
condition|)
block|{
name|gname
operator|=
literal|"get"
operator|+
name|gname
operator|.
name|substring
argument_list|(
literal|3
argument_list|)
expr_stmt|;
try|try
block|{
name|getter
operator|=
name|setter
operator|.
name|getDeclaringClass
argument_list|()
operator|.
name|getMethod
argument_list|(
name|gname
argument_list|,
operator|(
name|Class
index|[]
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|// no getter -- don't worry about it...
if|if
condition|(
name|type
operator|==
name|Boolean
operator|.
name|class
condition|)
block|{
name|gname
operator|=
literal|"is"
operator|+
name|setter
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|3
argument_list|)
expr_stmt|;
try|try
block|{
name|getter
operator|=
name|setter
operator|.
name|getDeclaringClass
argument_list|()
operator|.
name|getMethod
argument_list|(
name|gname
argument_list|,
operator|(
name|Class
index|[]
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex2
parameter_list|)
block|{
comment|// no getter -- don't worry about it...
block|}
block|}
block|}
block|}
block|}
block|}
DECL|method|storeName
specifier|private
name|void
name|storeName
parameter_list|(
name|Field
name|annotation
parameter_list|)
block|{
if|if
condition|(
name|annotation
operator|.
name|value
argument_list|()
operator|.
name|equals
argument_list|(
name|DEFAULT
argument_list|)
condition|)
block|{
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|name
operator|=
name|field
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|String
name|setterName
init|=
name|setter
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|setterName
operator|.
name|startsWith
argument_list|(
literal|"set"
argument_list|)
operator|&&
name|setterName
operator|.
name|length
argument_list|()
operator|>
literal|3
condition|)
block|{
name|name
operator|=
name|setterName
operator|.
name|substring
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|+
name|setterName
operator|.
name|substring
argument_list|(
literal|4
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|name
operator|=
name|setter
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|annotation
operator|.
name|value
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|'*'
argument_list|)
operator|>=
literal|0
condition|)
block|{
comment|//dynamic fields are annotated as @Field("categories_*")
comment|//if the field was annotated as a dynamic field, convert the name into a pattern
comment|//the wildcard (*) is supposed to be either a prefix or a suffix, hence the use of replaceFirst
name|name
operator|=
name|annotation
operator|.
name|value
argument_list|()
operator|.
name|replaceFirst
argument_list|(
literal|"\\*"
argument_list|,
literal|"\\.*"
argument_list|)
expr_stmt|;
name|dynamicFieldNamePatternMatcher
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^"
operator|+
name|name
operator|+
literal|"$"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|name
operator|=
name|annotation
operator|.
name|value
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|storeType
specifier|private
name|void
name|storeType
parameter_list|()
block|{
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|type
operator|=
name|field
operator|.
name|getType
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Class
index|[]
name|params
init|=
name|setter
operator|.
name|getParameterTypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|length
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|BindingException
argument_list|(
literal|"Invalid setter method. Must have one and only one parameter"
argument_list|)
throw|;
block|}
name|type
operator|=
name|params
index|[
literal|0
index|]
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|==
name|Collection
operator|.
name|class
operator|||
name|type
operator|==
name|List
operator|.
name|class
operator|||
name|type
operator|==
name|ArrayList
operator|.
name|class
condition|)
block|{
name|isList
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|annotation
operator|.
name|child
argument_list|()
condition|)
block|{
name|populateChild
argument_list|(
name|field
operator|.
name|getGenericType
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|type
operator|=
name|Object
operator|.
name|class
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|byte
index|[]
operator|.
name|class
condition|)
block|{
comment|//no op
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|isArray
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|annotation
operator|.
name|child
argument_list|()
condition|)
block|{
name|populateChild
argument_list|(
name|type
operator|.
name|getComponentType
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|type
operator|=
name|type
operator|.
name|getComponentType
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Map
operator|.
name|class
operator|||
name|type
operator|==
name|HashMap
operator|.
name|class
condition|)
block|{
comment|//corresponding to the support for dynamicFields
if|if
condition|(
name|annotation
operator|.
name|child
argument_list|()
condition|)
throw|throw
operator|new
name|BindingException
argument_list|(
literal|"Map should is not a valid type for a child document"
argument_list|)
throw|;
name|isContainedInMap
operator|=
literal|true
expr_stmt|;
comment|//assigned a default type
name|type
operator|=
name|Object
operator|.
name|class
expr_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|field
operator|.
name|getGenericType
argument_list|()
operator|instanceof
name|ParameterizedType
condition|)
block|{
comment|//check what are the generic values
name|ParameterizedType
name|parameterizedType
init|=
operator|(
name|ParameterizedType
operator|)
name|field
operator|.
name|getGenericType
argument_list|()
decl_stmt|;
name|Type
index|[]
name|types
init|=
name|parameterizedType
operator|.
name|getActualTypeArguments
argument_list|()
decl_stmt|;
if|if
condition|(
name|types
operator|!=
literal|null
operator|&&
name|types
operator|.
name|length
operator|==
literal|2
operator|&&
name|types
index|[
literal|0
index|]
operator|==
name|String
operator|.
name|class
condition|)
block|{
comment|//the key should always be String
comment|//Raw and primitive types
if|if
condition|(
name|types
index|[
literal|1
index|]
operator|instanceof
name|Class
condition|)
block|{
comment|//the value could be multivalued then it is a List, Collection, ArrayList
if|if
condition|(
name|types
index|[
literal|1
index|]
operator|==
name|Collection
operator|.
name|class
operator|||
name|types
index|[
literal|1
index|]
operator|==
name|List
operator|.
name|class
operator|||
name|types
index|[
literal|1
index|]
operator|==
name|ArrayList
operator|.
name|class
condition|)
block|{
name|type
operator|=
name|Object
operator|.
name|class
expr_stmt|;
name|isList
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|//else assume it is a primitive and put in the source type itself
name|type
operator|=
operator|(
name|Class
operator|)
name|types
index|[
literal|1
index|]
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|types
index|[
literal|1
index|]
operator|instanceof
name|ParameterizedType
condition|)
block|{
comment|//Of all the Parameterized types, only List is supported
name|Type
name|rawType
init|=
operator|(
operator|(
name|ParameterizedType
operator|)
name|types
index|[
literal|1
index|]
operator|)
operator|.
name|getRawType
argument_list|()
decl_stmt|;
if|if
condition|(
name|rawType
operator|==
name|Collection
operator|.
name|class
operator|||
name|rawType
operator|==
name|List
operator|.
name|class
operator|||
name|rawType
operator|==
name|ArrayList
operator|.
name|class
condition|)
block|{
name|type
operator|=
name|Object
operator|.
name|class
expr_stmt|;
name|isList
operator|=
literal|true
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|types
index|[
literal|1
index|]
operator|instanceof
name|GenericArrayType
condition|)
block|{
comment|//Array types
name|type
operator|=
call|(
name|Class
call|)
argument_list|(
operator|(
name|GenericArrayType
operator|)
name|types
index|[
literal|1
index|]
argument_list|)
operator|.
name|getGenericComponentType
argument_list|()
expr_stmt|;
name|isArray
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|//Throw an Exception if types are not known
throw|throw
operator|new
name|BindingException
argument_list|(
literal|"Allowed type for values of mapping a dynamicField are : "
operator|+
literal|"Object, Object[] and List"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|annotation
operator|.
name|child
argument_list|()
condition|)
block|{
name|populateChild
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|populateChild
specifier|private
name|void
name|populateChild
parameter_list|(
name|Type
name|typ
parameter_list|)
block|{
if|if
condition|(
name|typ
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"no type information available for"
operator|+
operator|(
name|field
operator|==
literal|null
condition|?
name|setter
else|:
name|field
operator|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|typ
operator|.
name|getClass
argument_list|()
operator|==
name|Class
operator|.
name|class
condition|)
block|{
comment|//of type class
name|type
operator|=
operator|(
name|Class
operator|)
name|typ
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|typ
operator|instanceof
name|ParameterizedType
condition|)
block|{
try|try
block|{
name|type
operator|=
name|Class
operator|.
name|forName
argument_list|(
operator|(
operator|(
name|ParameterizedType
operator|)
name|typ
operator|)
operator|.
name|getActualTypeArguments
argument_list|()
index|[
literal|0
index|]
operator|.
name|getTypeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BindingException
argument_list|(
literal|"Invalid type information available for"
operator|+
operator|(
name|field
operator|==
literal|null
condition|?
name|setter
else|:
name|field
operator|)
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|BindingException
argument_list|(
literal|"Invalid type information available for"
operator|+
operator|(
name|field
operator|==
literal|null
condition|?
name|setter
else|:
name|field
operator|)
argument_list|)
throw|;
block|}
name|child
operator|=
name|getDocFields
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
comment|/**      * Called by the {@link #inject} method to read the value(s) for a field      * This method supports reading of all "matching" fieldName's in the<code>SolrDocument</code>      *      * Returns<code>SolrDocument.getFieldValue</code> for regular fields,      * and<code>Map<String, List<Object>></code> for a dynamic field. The key is all matching fieldName's.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getFieldValue
specifier|private
name|Object
name|getFieldValue
parameter_list|(
name|SolrDocument
name|solrDocument
parameter_list|)
block|{
if|if
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|SolrDocument
argument_list|>
name|children
init|=
name|solrDocument
operator|.
name|getChildDocuments
argument_list|()
decl_stmt|;
if|if
condition|(
name|children
operator|==
literal|null
operator|||
name|children
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|isList
condition|)
block|{
name|ArrayList
name|list
init|=
operator|new
name|ArrayList
argument_list|(
name|children
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SolrDocument
name|c
range|:
name|children
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|getBean
argument_list|(
name|type
argument_list|,
name|child
argument_list|,
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
elseif|else
if|if
condition|(
name|isArray
condition|)
block|{
name|Object
index|[]
name|arr
init|=
operator|(
name|Object
index|[]
operator|)
name|Array
operator|.
name|newInstance
argument_list|(
name|type
argument_list|,
name|children
operator|.
name|size
argument_list|()
argument_list|)
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
name|children
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|arr
index|[
name|i
index|]
operator|=
name|getBean
argument_list|(
name|type
argument_list|,
name|child
argument_list|,
name|children
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|arr
return|;
block|}
else|else
block|{
return|return
name|getBean
argument_list|(
name|type
argument_list|,
name|child
argument_list|,
name|children
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
block|}
name|Object
name|fieldValue
init|=
name|solrDocument
operator|.
name|getFieldValue
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldValue
operator|!=
literal|null
condition|)
block|{
comment|//this is not a dynamic field. so return the value
return|return
name|fieldValue
return|;
block|}
if|if
condition|(
name|dynamicFieldNamePatternMatcher
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|//reading dynamic field values
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|allValuesMap
init|=
literal|null
decl_stmt|;
name|List
name|allValuesList
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|isContainedInMap
condition|)
block|{
name|allValuesMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|allValuesList
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|String
name|field
range|:
name|solrDocument
operator|.
name|getFieldNames
argument_list|()
control|)
block|{
if|if
condition|(
name|dynamicFieldNamePatternMatcher
operator|.
name|matcher
argument_list|(
name|field
argument_list|)
operator|.
name|find
argument_list|()
condition|)
block|{
name|Object
name|val
init|=
name|solrDocument
operator|.
name|getFieldValue
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|isContainedInMap
condition|)
block|{
if|if
condition|(
name|isList
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|val
operator|instanceof
name|List
operator|)
condition|)
block|{
name|List
name|al
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|al
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|val
operator|=
name|al
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|isArray
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|val
operator|instanceof
name|List
operator|)
condition|)
block|{
name|Object
index|[]
name|arr
init|=
operator|(
name|Object
index|[]
operator|)
name|Array
operator|.
name|newInstance
argument_list|(
name|type
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|arr
index|[
literal|0
index|]
operator|=
name|val
expr_stmt|;
name|val
operator|=
name|arr
expr_stmt|;
block|}
else|else
block|{
name|val
operator|=
name|Array
operator|.
name|newInstance
argument_list|(
name|type
argument_list|,
operator|(
operator|(
name|List
operator|)
name|val
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|allValuesMap
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|val
operator|instanceof
name|Collection
condition|)
block|{
name|allValuesList
operator|.
name|addAll
argument_list|(
operator|(
name|Collection
operator|)
name|val
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|allValuesList
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|isContainedInMap
condition|)
block|{
return|return
name|allValuesMap
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|allValuesMap
return|;
block|}
else|else
block|{
return|return
name|allValuesList
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|allValuesList
return|;
block|}
block|}
DECL|method|inject
parameter_list|<
name|T
parameter_list|>
name|void
name|inject
parameter_list|(
name|T
name|obj
parameter_list|,
name|SolrDocument
name|sdoc
parameter_list|)
block|{
name|Object
name|val
init|=
name|getFieldValue
argument_list|(
name|sdoc
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|isArray
operator|&&
operator|!
name|isContainedInMap
condition|)
block|{
name|List
name|list
decl_stmt|;
if|if
condition|(
name|val
operator|.
name|getClass
argument_list|()
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|set
argument_list|(
name|obj
argument_list|,
name|val
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|List
condition|)
block|{
name|list
operator|=
operator|(
name|List
operator|)
name|val
expr_stmt|;
block|}
else|else
block|{
name|list
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
name|set
argument_list|(
name|obj
argument_list|,
name|list
operator|.
name|toArray
argument_list|(
operator|(
name|Object
index|[]
operator|)
name|Array
operator|.
name|newInstance
argument_list|(
name|type
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isList
operator|&&
operator|!
name|isContainedInMap
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|val
operator|instanceof
name|List
operator|)
condition|)
block|{
name|List
name|list
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|val
operator|=
name|list
expr_stmt|;
block|}
name|set
argument_list|(
name|obj
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isContainedInMap
condition|)
block|{
if|if
condition|(
name|val
operator|instanceof
name|Map
condition|)
block|{
name|set
argument_list|(
name|obj
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|set
argument_list|(
name|obj
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|set
specifier|private
name|void
name|set
parameter_list|(
name|Object
name|obj
parameter_list|,
name|Object
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|!=
literal|null
operator|&&
name|type
operator|==
name|ByteBuffer
operator|.
name|class
operator|&&
name|v
operator|.
name|getClass
argument_list|()
operator|==
name|byte
index|[]
operator|.
name|class
condition|)
block|{
name|v
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|(
name|byte
index|[]
operator|)
name|v
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|field
operator|.
name|set
argument_list|(
name|obj
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|setter
operator|!=
literal|null
condition|)
block|{
name|setter
operator|.
name|invoke
argument_list|(
name|obj
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BindingException
argument_list|(
literal|"Exception while setting value : "
operator|+
name|v
operator|+
literal|" on "
operator|+
operator|(
name|field
operator|!=
literal|null
condition|?
name|field
else|:
name|setter
operator|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|get
specifier|public
name|Object
name|get
parameter_list|(
specifier|final
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|field
operator|.
name|get
argument_list|(
name|obj
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BindingException
argument_list|(
literal|"Exception while getting value: "
operator|+
name|field
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|getter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|BindingException
argument_list|(
literal|"Missing getter for field: "
operator|+
name|name
operator|+
literal|" -- You can only call the 'get' for fields that have a field of 'get' method"
argument_list|)
throw|;
block|}
try|try
block|{
return|return
name|getter
operator|.
name|invoke
argument_list|(
name|obj
argument_list|,
operator|(
name|Object
index|[]
operator|)
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BindingException
argument_list|(
literal|"Exception while getting value: "
operator|+
name|getter
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|field|DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT
init|=
literal|"#default"
decl_stmt|;
block|}
end_class

end_unit

