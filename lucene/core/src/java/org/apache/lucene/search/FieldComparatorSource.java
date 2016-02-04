begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Provides a {@link FieldComparator} for custom field sorting.  *  * @lucene.experimental  *  */
end_comment

begin_class
DECL|class|FieldComparatorSource
specifier|public
specifier|abstract
class|class
name|FieldComparatorSource
block|{
comment|/**    * Creates a comparator for the field in the given index.    *     * @param fieldname    *          Name of the field to create comparator for.    * @return FieldComparator.    * @throws IOException    *           If an error occurs reading the index.    */
DECL|method|newComparator
specifier|public
specifier|abstract
name|FieldComparator
argument_list|<
name|?
argument_list|>
name|newComparator
parameter_list|(
name|String
name|fieldname
parameter_list|,
name|int
name|numHits
parameter_list|,
name|int
name|sortPos
parameter_list|,
name|boolean
name|reversed
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

