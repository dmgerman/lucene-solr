begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/** Represents hits returned by {@link  * IndexSearcher#search(Query,int,Sort)}.  */
end_comment

begin_class
DECL|class|TopFieldDocs
specifier|public
class|class
name|TopFieldDocs
extends|extends
name|TopDocs
block|{
comment|/** The fields which were used to sort results by. */
DECL|field|fields
specifier|public
name|SortField
index|[]
name|fields
decl_stmt|;
comment|/** Creates one of these objects.    * @param totalHits  Total number of hits for the query.    * @param scoreDocs  The top hits for the query.    * @param fields     The sort criteria used to find the top hits.    * @param maxScore   The maximum score encountered.    */
DECL|method|TopFieldDocs
specifier|public
name|TopFieldDocs
parameter_list|(
name|int
name|totalHits
parameter_list|,
name|ScoreDoc
index|[]
name|scoreDocs
parameter_list|,
name|SortField
index|[]
name|fields
parameter_list|,
name|float
name|maxScore
parameter_list|)
block|{
name|super
argument_list|(
name|totalHits
argument_list|,
name|scoreDocs
argument_list|,
name|maxScore
argument_list|)
expr_stmt|;
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
block|}
block|}
end_class

end_unit

