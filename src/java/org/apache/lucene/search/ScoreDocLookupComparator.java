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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Expert: Compares two ScoreDoc objects for sorting using a lookup table.  *  *<p>Created: Feb 3, 2004 9:59:14 AM   *   * @author  Tim Jones (Nacimiento Software)  * @since   lucene 1.4  * @version $Id$  */
end_comment

begin_interface
DECL|interface|ScoreDocLookupComparator
specifier|public
interface|interface
name|ScoreDocLookupComparator
extends|extends
name|ScoreDocComparator
block|{
comment|/** 	 * Verifies that the internal lookup table is the correct size.  This 	 * comparator uses a lookup table, so it is important to that the 	 * table matches the number of documents in the index. 	 * @param n  Expected size of table. 	 * @return   True if internal table matches expected size; false otherwise 	 */
DECL|method|sizeMatches
name|boolean
name|sizeMatches
parameter_list|(
name|int
name|n
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

