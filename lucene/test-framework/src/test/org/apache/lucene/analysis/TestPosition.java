begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_comment
comment|/**  * Trivial position class.  */
end_comment

begin_class
annotation|@
name|Ignore
DECL|class|TestPosition
specifier|public
class|class
name|TestPosition
extends|extends
name|LookaheadTokenFilter
operator|.
name|Position
block|{
DECL|field|fact
specifier|private
name|String
name|fact
decl_stmt|;
DECL|method|getFact
specifier|public
name|String
name|getFact
parameter_list|()
block|{
return|return
name|fact
return|;
block|}
DECL|method|setFact
specifier|public
name|void
name|setFact
parameter_list|(
name|String
name|fact
parameter_list|)
block|{
name|this
operator|.
name|fact
operator|=
name|fact
expr_stmt|;
block|}
block|}
end_class

end_unit

