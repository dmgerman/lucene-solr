begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
package|;
end_package

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
name|TokenStream
import|;
end_import

begin_comment
comment|/**  * Normalizes token text to lower case.  *<p>  * This class moved to Lucene Core, but a reference in the {@code analysis/common} module  * is preserved for documentation purposes and consistency with filter factory.  * @see org.apache.lucene.analysis.LowerCaseFilter  * @see LowerCaseFilterFactory  */
end_comment

begin_class
DECL|class|LowerCaseFilter
specifier|public
specifier|final
class|class
name|LowerCaseFilter
extends|extends
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|LowerCaseFilter
block|{
comment|/**    * Create a new LowerCaseFilter, that normalizes token text to lower case.    *     * @param in TokenStream to filter    */
DECL|method|LowerCaseFilter
specifier|public
name|LowerCaseFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

