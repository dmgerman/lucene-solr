begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.replicator
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Describes a file in a {@link Revision}. A file has a source, which allows a  * single revision to contain files from multiple sources (e.g. multiple  * indexes).  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|RevisionFile
specifier|public
class|class
name|RevisionFile
block|{
comment|/** The name of the file. */
DECL|field|fileName
specifier|public
specifier|final
name|String
name|fileName
decl_stmt|;
comment|/** The size of the file denoted by {@link #fileName}. */
DECL|field|size
specifier|public
name|long
name|size
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Constructor with the given file name. */
DECL|method|RevisionFile
specifier|public
name|RevisionFile
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
if|if
condition|(
name|fileName
operator|==
literal|null
operator|||
name|fileName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"fileName cannot be null or empty"
argument_list|)
throw|;
block|}
name|this
operator|.
name|fileName
operator|=
name|fileName
expr_stmt|;
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
name|RevisionFile
name|other
init|=
operator|(
name|RevisionFile
operator|)
name|obj
decl_stmt|;
return|return
name|fileName
operator|.
name|equals
argument_list|(
name|other
operator|.
name|fileName
argument_list|)
operator|&&
name|size
operator|==
name|other
operator|.
name|size
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|fileName
operator|.
name|hashCode
argument_list|()
operator|^
call|(
name|int
call|)
argument_list|(
name|size
operator|^
operator|(
name|size
operator|>>>
literal|32
operator|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"fileName="
operator|+
name|fileName
operator|+
literal|" size="
operator|+
name|size
return|;
block|}
block|}
end_class

end_unit

