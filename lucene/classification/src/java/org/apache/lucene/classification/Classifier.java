begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.classification
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|index
operator|.
name|AtomicReader
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

begin_comment
comment|/**  * A classifier, see<code>http://en.wikipedia.org/wiki/Classifier_(mathematics)</code>  */
end_comment

begin_interface
DECL|interface|Classifier
specifier|public
interface|interface
name|Classifier
block|{
comment|/**    * Assign a class to the given text String    * @param text a String containing text to be classified    * @return a String representing a class    * @throws IOException    */
DECL|method|assignClass
specifier|public
name|String
name|assignClass
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Train the classifier using the underlying Lucene index    * @param atomicReader the reader to use to access the Lucene index    * @param textFieldName the name of the field used to compare documents    * @param classFieldName the name of the field containing the class assigned to documents    * @param analyzer the analyzer used to tokenize / filter the unseen text    * @throws IOException    */
DECL|method|train
specifier|public
name|void
name|train
parameter_list|(
name|AtomicReader
name|atomicReader
parameter_list|,
name|String
name|textFieldName
parameter_list|,
name|String
name|classFieldName
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

