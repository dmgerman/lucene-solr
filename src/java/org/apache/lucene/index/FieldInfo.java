begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|FieldInfo
specifier|final
class|class
name|FieldInfo
block|{
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|isIndexed
name|boolean
name|isIndexed
decl_stmt|;
DECL|field|number
name|int
name|number
decl_stmt|;
comment|// true if term vector for this field should be stored
DECL|field|storeTermVector
name|boolean
name|storeTermVector
decl_stmt|;
DECL|field|storeOffsetWithTermVector
name|boolean
name|storeOffsetWithTermVector
decl_stmt|;
DECL|field|storePositionWithTermVector
name|boolean
name|storePositionWithTermVector
decl_stmt|;
DECL|field|omitNorms
name|boolean
name|omitNorms
decl_stmt|;
comment|// omit norms associated with indexed fields
DECL|field|storePayloads
name|boolean
name|storePayloads
decl_stmt|;
comment|// whether this field stores payloads together with term positions
DECL|method|FieldInfo
name|FieldInfo
parameter_list|(
name|String
name|na
parameter_list|,
name|boolean
name|tk
parameter_list|,
name|int
name|nu
parameter_list|,
name|boolean
name|storeTermVector
parameter_list|,
name|boolean
name|storePositionWithTermVector
parameter_list|,
name|boolean
name|storeOffsetWithTermVector
parameter_list|,
name|boolean
name|omitNorms
parameter_list|,
name|boolean
name|storePayloads
parameter_list|)
block|{
name|name
operator|=
name|na
expr_stmt|;
name|isIndexed
operator|=
name|tk
expr_stmt|;
name|number
operator|=
name|nu
expr_stmt|;
name|this
operator|.
name|storeTermVector
operator|=
name|storeTermVector
expr_stmt|;
name|this
operator|.
name|storeOffsetWithTermVector
operator|=
name|storeOffsetWithTermVector
expr_stmt|;
name|this
operator|.
name|storePositionWithTermVector
operator|=
name|storePositionWithTermVector
expr_stmt|;
name|this
operator|.
name|omitNorms
operator|=
name|omitNorms
expr_stmt|;
name|this
operator|.
name|storePayloads
operator|=
name|storePayloads
expr_stmt|;
block|}
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
return|return
operator|new
name|FieldInfo
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
name|number
argument_list|,
name|storeTermVector
argument_list|,
name|storePositionWithTermVector
argument_list|,
name|storeOffsetWithTermVector
argument_list|,
name|omitNorms
argument_list|,
name|storePayloads
argument_list|)
return|;
block|}
DECL|method|update
name|void
name|update
parameter_list|(
name|boolean
name|isIndexed
parameter_list|,
name|boolean
name|storeTermVector
parameter_list|,
name|boolean
name|storePositionWithTermVector
parameter_list|,
name|boolean
name|storeOffsetWithTermVector
parameter_list|,
name|boolean
name|omitNorms
parameter_list|,
name|boolean
name|storePayloads
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|isIndexed
operator|!=
name|isIndexed
condition|)
block|{
name|this
operator|.
name|isIndexed
operator|=
literal|true
expr_stmt|;
comment|// once indexed, always index
block|}
if|if
condition|(
name|this
operator|.
name|storeTermVector
operator|!=
name|storeTermVector
condition|)
block|{
name|this
operator|.
name|storeTermVector
operator|=
literal|true
expr_stmt|;
comment|// once vector, always vector
block|}
if|if
condition|(
name|this
operator|.
name|storePositionWithTermVector
operator|!=
name|storePositionWithTermVector
condition|)
block|{
name|this
operator|.
name|storePositionWithTermVector
operator|=
literal|true
expr_stmt|;
comment|// once vector, always vector
block|}
if|if
condition|(
name|this
operator|.
name|storeOffsetWithTermVector
operator|!=
name|storeOffsetWithTermVector
condition|)
block|{
name|this
operator|.
name|storeOffsetWithTermVector
operator|=
literal|true
expr_stmt|;
comment|// once vector, always vector
block|}
if|if
condition|(
name|this
operator|.
name|omitNorms
operator|!=
name|omitNorms
condition|)
block|{
name|this
operator|.
name|omitNorms
operator|=
literal|false
expr_stmt|;
comment|// once norms are stored, always store
block|}
if|if
condition|(
name|this
operator|.
name|storePayloads
operator|!=
name|storePayloads
condition|)
block|{
name|this
operator|.
name|storePayloads
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|update
name|void
name|update
parameter_list|(
name|FieldInfo
name|other
parameter_list|)
block|{
if|if
condition|(
name|isIndexed
operator|!=
name|other
operator|.
name|isIndexed
condition|)
block|{
name|isIndexed
operator|=
literal|true
expr_stmt|;
comment|// once indexed, always index
block|}
if|if
condition|(
name|storeTermVector
operator|!=
name|other
operator|.
name|storeTermVector
condition|)
block|{
name|storeTermVector
operator|=
literal|true
expr_stmt|;
comment|// once vector, always vector
block|}
if|if
condition|(
name|storePositionWithTermVector
operator|!=
name|other
operator|.
name|storePositionWithTermVector
condition|)
block|{
name|storePositionWithTermVector
operator|=
literal|true
expr_stmt|;
comment|// once vector, always vector
block|}
if|if
condition|(
name|storeOffsetWithTermVector
operator|!=
name|other
operator|.
name|storeOffsetWithTermVector
condition|)
block|{
name|storeOffsetWithTermVector
operator|=
literal|true
expr_stmt|;
comment|// once vector, always vector
block|}
if|if
condition|(
name|omitNorms
operator|!=
name|other
operator|.
name|omitNorms
condition|)
block|{
name|omitNorms
operator|=
literal|false
expr_stmt|;
comment|// once norms are stored, always store
block|}
if|if
condition|(
name|storePayloads
operator|!=
name|other
operator|.
name|storePayloads
condition|)
block|{
name|storePayloads
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

