begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|highlight
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
comment|/**  * {@link Fragmenter} implementation which does not fragment the text.  * This is useful for highlighting the entire content of a document or field.  */
end_comment

begin_class
DECL|class|NullFragmenter
specifier|public
class|class
name|NullFragmenter
implements|implements
name|Fragmenter
block|{
annotation|@
name|Override
DECL|method|start
specifier|public
name|void
name|start
parameter_list|(
name|String
name|s
parameter_list|,
name|TokenStream
name|tokenStream
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|isNewFragment
specifier|public
name|boolean
name|isNewFragment
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

