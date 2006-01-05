begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store.je
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|je
package|;
end_package

begin_comment
comment|/**  * Copyright 2002-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Lock
import|;
end_import

begin_comment
comment|/**  * Port of Andi Vajda's DbDirectory to Java Edition of Berkeley Database  *   * @author Aaron Donovan  */
end_comment

begin_class
DECL|class|JELock
specifier|public
class|class
name|JELock
extends|extends
name|Lock
block|{
DECL|field|isLocked
name|boolean
name|isLocked
init|=
literal|false
decl_stmt|;
DECL|method|JELock
specifier|public
name|JELock
parameter_list|()
block|{     }
DECL|method|obtain
specifier|public
name|boolean
name|obtain
parameter_list|()
block|{
return|return
operator|(
name|isLocked
operator|=
literal|true
operator|)
return|;
block|}
DECL|method|release
specifier|public
name|void
name|release
parameter_list|()
block|{
name|isLocked
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|isLocked
specifier|public
name|boolean
name|isLocked
parameter_list|()
block|{
return|return
name|isLocked
return|;
block|}
block|}
end_class

end_unit

