begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Simple Iterator of document Ids which may include score information.  *  *<p>  * The order of the documents is determined by the context in which the  * DocIterator instance was retrieved.  *</p>  *  * @version $Id$  */
end_comment

begin_interface
DECL|interface|DocIterator
specifier|public
interface|interface
name|DocIterator
extends|extends
name|Iterator
argument_list|<
name|Integer
argument_list|>
block|{
comment|// already declared in superclass, redeclaring prevents javadoc inheritance
comment|//public boolean hasNext();
comment|/**    * Returns the next document id if hasNext()==true    *    *<code>    * This method is equivalent to<code>next()</code>, but avoids the creation    * of an Integer Object.    * @see #next()    */
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
function_decl|;
comment|/**    * Returns the score for the document just returned by<code>nextDoc()</code>    *    *<p>    * The value returned may be meaningless depending on the context    * in which the DocIterator instance was retrieved.    */
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

