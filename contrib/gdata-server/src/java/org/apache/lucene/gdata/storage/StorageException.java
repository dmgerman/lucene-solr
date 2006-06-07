begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.storage
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|storage
package|;
end_package

begin_comment
comment|/**   * The StorageException will be throw if any error or exception inside the   * storage implementation occures. This exception hides all other exceptions   * from inside the storage.   *    * @author Simon Willnauer   *    */
end_comment

begin_class
DECL|class|StorageException
specifier|public
class|class
name|StorageException
extends|extends
name|Exception
block|{
comment|/**       *        */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|4997572416934126511L
decl_stmt|;
comment|/**       * Constructs a new StorageException       */
DECL|method|StorageException
specifier|public
name|StorageException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**       * Constructs a new StorageException       *        * @param message -       *            the exception message       */
DECL|method|StorageException
specifier|public
name|StorageException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|/**       * Constructs a new StorageException       *        * @param message -       *            the exception message       * @param cause -       *            the root cause of this exception       */
DECL|method|StorageException
specifier|public
name|StorageException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
comment|/**       * Constructs a new StorageException       *        * @param cause -       *            the root cause of this exception       */
DECL|method|StorageException
specifier|public
name|StorageException
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

