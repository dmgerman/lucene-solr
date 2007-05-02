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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|IndexReader
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  * Expert: returns a comparator for sorting ScoreDocs.  *  *<p>Created: Apr 21, 2004 3:49:28 PM  *   * @author  Tim Jones  * @version $Id$  * @since   1.4  */
end_comment

begin_interface
DECL|interface|SortComparatorSource
specifier|public
interface|interface
name|SortComparatorSource
extends|extends
name|Serializable
block|{
comment|/**    * Creates a comparator for the field in the given index.    * @param reader Index to create comparator for.    * @param fieldname  Name of the field to create comparator for.    * @return Comparator of ScoreDoc objects.    * @throws IOException If an error occurs reading the index.    */
DECL|method|newComparator
name|ScoreDocComparator
name|newComparator
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|fieldname
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

