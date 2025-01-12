begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_comment
comment|/**  * A convenient class which offers a semi-immutable object wrapper  * implementation which allows one to set the value of an object exactly once,  * and retrieve it many times. If {@link #set(Object)} is called more than once,  * {@link AlreadySetException} is thrown and the operation  * will fail.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SetOnce
specifier|public
specifier|final
class|class
name|SetOnce
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Cloneable
block|{
comment|/** Thrown when {@link SetOnce#set(Object)} is called more than once. */
DECL|class|AlreadySetException
specifier|public
specifier|static
specifier|final
class|class
name|AlreadySetException
extends|extends
name|IllegalStateException
block|{
DECL|method|AlreadySetException
specifier|public
name|AlreadySetException
parameter_list|()
block|{
name|super
argument_list|(
literal|"The object cannot be set twice!"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|obj
specifier|private
specifier|volatile
name|T
name|obj
init|=
literal|null
decl_stmt|;
DECL|field|set
specifier|private
specifier|final
name|AtomicBoolean
name|set
decl_stmt|;
comment|/**    * A default constructor which does not set the internal object, and allows    * setting it by calling {@link #set(Object)}.    */
DECL|method|SetOnce
specifier|public
name|SetOnce
parameter_list|()
block|{
name|set
operator|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new instance with the internal object set to the given object.    * Note that any calls to {@link #set(Object)} afterwards will result in    * {@link AlreadySetException}    *    * @throws AlreadySetException if called more than once    * @see #set(Object)    */
DECL|method|SetOnce
specifier|public
name|SetOnce
parameter_list|(
name|T
name|obj
parameter_list|)
block|{
name|this
operator|.
name|obj
operator|=
name|obj
expr_stmt|;
name|set
operator|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Sets the given object. If the object has already been set, an exception is thrown. */
DECL|method|set
specifier|public
specifier|final
name|void
name|set
parameter_list|(
name|T
name|obj
parameter_list|)
block|{
if|if
condition|(
name|set
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|this
operator|.
name|obj
operator|=
name|obj
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|AlreadySetException
argument_list|()
throw|;
block|}
block|}
comment|/** Returns the object set by {@link #set(Object)}. */
DECL|method|get
specifier|public
specifier|final
name|T
name|get
parameter_list|()
block|{
return|return
name|obj
return|;
block|}
block|}
end_class

end_unit

