begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Sneaky: rethrowing checked exceptions as unchecked  * ones. Eh, it is sometimes useful...  *  *<p>Pulled from<a href="http://www.javapuzzlers.com">Java Puzzlers</a>.</p>  * @see "http://www.amazon.com/Java-Puzzlers-Traps-Pitfalls-Corner/dp/032133678X"  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|class|Rethrow
specifier|public
specifier|final
class|class
name|Rethrow
block|{
comment|/**    * Classy puzzler to rethrow any checked exception as an unchecked one.    */
DECL|class|Rethrower
specifier|private
specifier|static
class|class
name|Rethrower
parameter_list|<
name|T
extends|extends
name|Throwable
parameter_list|>
block|{
DECL|method|rethrow
specifier|private
name|void
name|rethrow
parameter_list|(
name|Throwable
name|t
parameter_list|)
throws|throws
name|T
block|{
throw|throw
operator|(
name|T
operator|)
name|t
throw|;
block|}
block|}
comment|/**    * Rethrows<code>t</code> (identical object).    */
DECL|method|rethrow
specifier|public
specifier|static
name|void
name|rethrow
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
operator|new
name|Rethrower
argument_list|<
name|Error
argument_list|>
argument_list|()
operator|.
name|rethrow
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

