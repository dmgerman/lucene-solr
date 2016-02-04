begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.store.blockcache
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|store
operator|.
name|blockcache
package|;
end_package

begin_comment
comment|/**  * @lucene.experimental  */
end_comment

begin_class
DECL|class|BlockCacheKey
specifier|public
class|class
name|BlockCacheKey
implements|implements
name|Cloneable
block|{
DECL|field|block
specifier|private
name|long
name|block
decl_stmt|;
DECL|field|file
specifier|private
name|int
name|file
decl_stmt|;
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|method|getPath
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
DECL|method|setPath
specifier|public
name|void
name|setPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
DECL|method|getBlock
specifier|public
name|long
name|getBlock
parameter_list|()
block|{
return|return
name|block
return|;
block|}
DECL|method|getFile
specifier|public
name|int
name|getFile
parameter_list|()
block|{
return|return
name|file
return|;
block|}
DECL|method|setBlock
specifier|public
name|void
name|setBlock
parameter_list|(
name|long
name|block
parameter_list|)
block|{
name|this
operator|.
name|block
operator|=
name|block
expr_stmt|;
block|}
DECL|method|setFile
specifier|public
name|void
name|setFile
parameter_list|(
name|int
name|file
parameter_list|)
block|{
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|block
operator|^
operator|(
name|block
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|file
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|path
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|path
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|BlockCacheKey
name|other
init|=
operator|(
name|BlockCacheKey
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|block
operator|!=
name|other
operator|.
name|block
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|file
operator|!=
name|other
operator|.
name|file
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|path
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|path
operator|.
name|equals
argument_list|(
name|other
operator|.
name|path
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|BlockCacheKey
name|clone
parameter_list|()
block|{
try|try
block|{
return|return
operator|(
name|BlockCacheKey
operator|)
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

