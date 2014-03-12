begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

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
name|Arrays
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * This transformer does 3 things  *<ul>  *<li>It turns every row into 3 rows,   *     modifying any "id" column to ensure duplicate entries in the index  *<li>The 2nd Row has 2x values for every column,   *   with the added one being backwards of the original  *<li>The 3rd Row has an added static value  *</ul>  *   * Also, this does not extend Transformer.  */
end_comment

begin_class
DECL|class|TripleThreatTransformer
specifier|public
class|class
name|TripleThreatTransformer
block|{
DECL|method|transformRow
specifier|public
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
parameter_list|)
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rows
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|row
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|addDuplicateBackwardsValues
argument_list|(
name|row
argument_list|)
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|row
argument_list|)
argument_list|)
expr_stmt|;
name|rows
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|put
argument_list|(
literal|"AddAColumn_s"
argument_list|,
literal|"Added"
argument_list|)
expr_stmt|;
name|modifyIdColumn
argument_list|(
name|rows
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|modifyIdColumn
argument_list|(
name|rows
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|2
argument_list|)
expr_stmt|;
return|return
name|rows
return|;
block|}
DECL|method|addDuplicateBackwardsValues
specifier|private
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|addDuplicateBackwardsValues
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
parameter_list|)
block|{
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|n
init|=
operator|new
name|LinkedHashMap
argument_list|<>
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
name|Object
argument_list|>
name|entry
range|:
name|row
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
if|if
condition|(
operator|!
literal|"id"
operator|.
name|equalsIgnoreCase
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|String
index|[]
name|vals
init|=
operator|new
name|String
index|[
literal|2
index|]
decl_stmt|;
name|vals
index|[
literal|0
index|]
operator|=
name|entry
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|?
literal|"null"
else|:
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|vals
index|[
literal|1
index|]
operator|=
operator|new
name|StringBuilder
argument_list|(
name|vals
index|[
literal|0
index|]
argument_list|)
operator|.
name|reverse
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|n
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|vals
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|n
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|n
return|;
block|}
DECL|method|modifyIdColumn
specifier|private
name|void
name|modifyIdColumn
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
parameter_list|,
name|int
name|num
parameter_list|)
block|{
name|Object
name|o
init|=
name|row
operator|.
name|remove
argument_list|(
literal|"ID"
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
name|o
operator|=
name|row
operator|.
name|remove
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
name|String
name|id
init|=
name|o
operator|.
name|toString
argument_list|()
decl_stmt|;
name|id
operator|=
literal|"TripleThreat-"
operator|+
name|num
operator|+
literal|"-"
operator|+
name|id
expr_stmt|;
name|row
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

