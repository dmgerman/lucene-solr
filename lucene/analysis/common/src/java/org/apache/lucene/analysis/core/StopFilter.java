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
name|CharArraySet
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
name|TokenStream
import|;
end_import

begin_comment
comment|/**  * Removes stop words from a token stream.  *<p>  * This class moved to Lucene Core, but a reference in the {@code analysis/common} module  * is preserved for documentation purposes and consistency with filter factory.  * @see org.apache.lucene.analysis.StopFilter  * @see StopFilterFactory  */
end_comment

begin_class
DECL|class|StopFilter
specifier|public
specifier|final
class|class
name|StopFilter
extends|extends
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|StopFilter
block|{
comment|/**    * Constructs a filter which removes words from the input TokenStream that are    * named in the Set.    *     * @param in    *          Input stream    * @param stopWords    *          A {@link CharArraySet} representing the stopwords.    * @see #makeStopSet(java.lang.String...)    */
DECL|method|StopFilter
specifier|public
name|StopFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|CharArraySet
name|stopWords
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|,
name|stopWords
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

