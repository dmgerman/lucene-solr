begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.suggest.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
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
name|codecs
operator|.
name|PostingsFormat
import|;
end_import

begin_comment
comment|/**  * {@link org.apache.lucene.search.suggest.document.CompletionPostingsFormat}  * for {@link org.apache.lucene.codecs.lucene50.Lucene50PostingsFormat}  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Completion50PostingsFormat
specifier|public
class|class
name|Completion50PostingsFormat
extends|extends
name|CompletionPostingsFormat
block|{
comment|/**    * Sole Constructor    */
DECL|method|Completion50PostingsFormat
specifier|public
name|Completion50PostingsFormat
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|delegatePostingsFormat
specifier|protected
name|PostingsFormat
name|delegatePostingsFormat
parameter_list|()
block|{
return|return
name|PostingsFormat
operator|.
name|forName
argument_list|(
literal|"Lucene50"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

