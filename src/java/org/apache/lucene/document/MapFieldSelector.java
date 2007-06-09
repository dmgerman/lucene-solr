begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
comment|/**  * A FieldSelector based on a Map of field names to FieldSelectorResults  *  * @author Chuck Williams  */
end_comment

begin_class
DECL|class|MapFieldSelector
specifier|public
class|class
name|MapFieldSelector
implements|implements
name|FieldSelector
block|{
DECL|field|fieldSelections
name|Map
name|fieldSelections
decl_stmt|;
comment|/** Create a a MapFieldSelector      * @param fieldSelections maps from field names (String) to FieldSelectorResults      */
DECL|method|MapFieldSelector
specifier|public
name|MapFieldSelector
parameter_list|(
name|Map
name|fieldSelections
parameter_list|)
block|{
name|this
operator|.
name|fieldSelections
operator|=
name|fieldSelections
expr_stmt|;
block|}
comment|/** Create a a MapFieldSelector      * @param fields fields to LOAD.  List of Strings.  All other fields are NO_LOAD.      */
DECL|method|MapFieldSelector
specifier|public
name|MapFieldSelector
parameter_list|(
name|List
name|fields
parameter_list|)
block|{
name|fieldSelections
operator|=
operator|new
name|HashMap
argument_list|(
name|fields
operator|.
name|size
argument_list|()
operator|*
literal|5
operator|/
literal|3
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
name|fields
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|fieldSelections
operator|.
name|put
argument_list|(
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|FieldSelectorResult
operator|.
name|LOAD
argument_list|)
expr_stmt|;
block|}
comment|/** Create a a MapFieldSelector      * @param fields fields to LOAD.  All other fields are NO_LOAD.      */
DECL|method|MapFieldSelector
specifier|public
name|MapFieldSelector
parameter_list|(
name|String
index|[]
name|fields
parameter_list|)
block|{
name|fieldSelections
operator|=
operator|new
name|HashMap
argument_list|(
name|fields
operator|.
name|length
operator|*
literal|5
operator|/
literal|3
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|fieldSelections
operator|.
name|put
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|,
name|FieldSelectorResult
operator|.
name|LOAD
argument_list|)
expr_stmt|;
block|}
comment|/** Load field according to its associated value in fieldSelections      * @param field a field name      * @return the fieldSelections value that field maps to or NO_LOAD if none.      */
DECL|method|accept
specifier|public
name|FieldSelectorResult
name|accept
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|FieldSelectorResult
name|selection
init|=
operator|(
name|FieldSelectorResult
operator|)
name|fieldSelections
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
return|return
name|selection
operator|!=
literal|null
condition|?
name|selection
else|:
name|FieldSelectorResult
operator|.
name|NO_LOAD
return|;
block|}
block|}
end_class

end_unit

