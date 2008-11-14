begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_comment
comment|/**  * CharStream adds<a href="#correctOffset(int)">correctOffset</a> functionality over Reader.  *  * @version $Id$  * @since Solr 1.4  *  */
end_comment

begin_class
DECL|class|CharStream
specifier|public
specifier|abstract
class|class
name|CharStream
extends|extends
name|Reader
block|{
comment|/**    * called by CharFilter(s) and Tokenizer to correct token offset.    *    * @param currentOff current offset    * @return corrected token offset    */
DECL|method|correctOffset
specifier|public
specifier|abstract
name|int
name|correctOffset
parameter_list|(
name|int
name|currentOff
parameter_list|)
function_decl|;
block|}
end_class

end_unit

