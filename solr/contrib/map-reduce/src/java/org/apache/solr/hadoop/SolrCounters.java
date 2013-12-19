begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.hadoop
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|hadoop
package|;
end_package

begin_enum
DECL|enum|SolrCounters
specifier|public
enum|enum
name|SolrCounters
block|{
DECL|enum constant|DOCUMENTS_WRITTEN
DECL|enum constant|getClassName
name|DOCUMENTS_WRITTEN
argument_list|(
name|getClassName
argument_list|(
name|SolrReducer
operator|.
name|class
argument_list|)
DECL|enum constant|Ó
operator|+
literal|": Number of documents processed"
argument_list|)
block|,
DECL|enum constant|BATCHES_WRITTEN
DECL|enum constant|getClassName
name|BATCHES_WRITTEN
argument_list|(
name|getClassName
argument_list|(
name|SolrReducer
operator|.
name|class
argument_list|)
DECL|enum constant|Ó
operator|+
literal|": Number of document batches processed"
argument_list|)
block|,
DECL|enum constant|BATCH_WRITE_TIME
DECL|enum constant|getClassName
name|BATCH_WRITE_TIME
argument_list|(
name|getClassName
argument_list|(
name|SolrReducer
operator|.
name|class
argument_list|)
DECL|enum constant|Ó
operator|+
literal|": Time spent by reducers writing batches [ms]"
argument_list|)
block|,
DECL|enum constant|PHYSICAL_REDUCER_MERGE_TIME
DECL|enum constant|getClassName
name|PHYSICAL_REDUCER_MERGE_TIME
argument_list|(
name|getClassName
argument_list|(
name|SolrReducer
operator|.
name|class
argument_list|)
DECL|enum constant|Ó
operator|+
literal|": Time spent by reducers on physical merges [ms]"
argument_list|)
block|,
DECL|enum constant|LOGICAL_TREE_MERGE_TIME
DECL|enum constant|getClassName
name|LOGICAL_TREE_MERGE_TIME
argument_list|(
name|getClassName
argument_list|(
name|TreeMergeMapper
operator|.
name|class
argument_list|)
DECL|enum constant|Ó
operator|+
literal|": Time spent on logical tree merges [ms]"
argument_list|)
block|,
DECL|enum constant|PHYSICAL_TREE_MERGE_TIME
DECL|enum constant|getClassName
name|PHYSICAL_TREE_MERGE_TIME
argument_list|(
name|getClassName
argument_list|(
name|TreeMergeMapper
operator|.
name|class
argument_list|)
DECL|enum constant|Ó
operator|+
literal|": Time spent on physical tree merges [ms]"
argument_list|)
block|;
DECL|field|label
specifier|private
specifier|final
name|String
name|label
decl_stmt|;
DECL|method|SolrCounters
specifier|private
name|SolrCounters
parameter_list|(
name|String
name|label
parameter_list|)
block|{
name|this
operator|.
name|label
operator|=
name|label
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|label
return|;
block|}
DECL|method|getClassName
specifier|private
specifier|static
name|String
name|getClassName
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
return|return
name|Utils
operator|.
name|getShortClassName
argument_list|(
name|clazz
argument_list|)
return|;
block|}
block|}
end_enum

end_unit

