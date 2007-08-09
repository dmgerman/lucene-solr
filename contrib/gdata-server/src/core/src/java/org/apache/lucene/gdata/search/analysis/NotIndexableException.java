begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.search.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
operator|.
name|analysis
package|;
end_package

begin_comment
comment|/**  * This exception will be thrown by ContentStrategy instances if an exception  * occurs while retrieving content from entries  *   *  *   */
end_comment

begin_class
DECL|class|NotIndexableException
specifier|public
class|class
name|NotIndexableException
extends|extends
name|Exception
block|{
comment|/**      *       */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1538388864181786380L
decl_stmt|;
comment|/**      * Constructs a new NotIndexableException      */
DECL|method|NotIndexableException
specifier|public
name|NotIndexableException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**      * Constructs a new NotIndexableException with the specified detail message.      *       * @param arg0 -      *            detail message      */
DECL|method|NotIndexableException
specifier|public
name|NotIndexableException
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
name|super
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new NotIndexableException with the specified detail message      * and nested exception.      *       * @param arg0 -      *            detail message      * @param arg1 -      *            nested exception      */
DECL|method|NotIndexableException
specifier|public
name|NotIndexableException
parameter_list|(
name|String
name|arg0
parameter_list|,
name|Throwable
name|arg1
parameter_list|)
block|{
name|super
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new NotIndexableException with a nested exception caused      * this exception.      *       * @param arg0 -      *            nested exception      */
DECL|method|NotIndexableException
specifier|public
name|NotIndexableException
parameter_list|(
name|Throwable
name|arg0
parameter_list|)
block|{
name|super
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

