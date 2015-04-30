begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|util
operator|.
name|List
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
name|LeafReader
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
name|search
operator|.
name|Query
import|;
end_import

begin_comment
comment|/**  * A classifier, see<code>http://en.wikipedia.org/wiki/Classifier_(mathematics)</code>, which assign classes of type  *<code>T</code>  *  * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|Classifier
specifier|public
interface|interface
name|Classifier
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**    * Assign a class (with score) to the given text String    *    * @param text a String containing text to be classified    * @return a {@link ClassificationResult} holding assigned class of type<code>T</code> and score    * @throws IOException If there is a low-level I/O error.    */
DECL|method|assignClass
name|ClassificationResult
argument_list|<
name|T
argument_list|>
name|assignClass
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get all the classes (sorted by score, descending) assigned to the given text String.    *    * @param text a String containing text to be classified    * @return the whole list of {@link ClassificationResult}, the classes and scores. Returns<code>null</code> if the classifier can't make lists.    * @throws IOException If there is a low-level I/O error.    */
DECL|method|getClasses
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|T
argument_list|>
argument_list|>
name|getClasses
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the first<code>max</code> classes (sorted by score, descending) assigned to the given text String.    *    * @param text a String containing text to be classified    * @param max  the number of return list elements    * @return the whole list of {@link ClassificationResult}, the classes and scores. Cut for "max" number of elements. Returns<code>null</code> if the classifier can't make lists.    * @throws IOException If there is a low-level I/O error.    */
DECL|method|getClasses
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|T
argument_list|>
argument_list|>
name|getClasses
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

