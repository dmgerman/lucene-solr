begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * @author yonik  * @version $Id$  */
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
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
function_decl|;
comment|/**    * returns the next document id if hasNext()==true    */
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
function_decl|;
comment|/**    * returns the score for the document just returned by nextDoc()    */
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

