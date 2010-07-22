begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * This exception is thrown when Lucene detects  * an index that is too old for this Lucene version  */
end_comment

begin_class
DECL|class|IndexFormatTooOldException
specifier|public
class|class
name|IndexFormatTooOldException
extends|extends
name|CorruptIndexException
block|{
DECL|method|IndexFormatTooOldException
specifier|public
name|IndexFormatTooOldException
parameter_list|(
name|String
name|filename
parameter_list|,
name|int
name|version
parameter_list|,
name|int
name|minVersion
parameter_list|,
name|int
name|maxVersion
parameter_list|)
block|{
name|super
argument_list|(
literal|"Format version is not supported"
operator|+
operator|(
name|filename
operator|!=
literal|null
condition|?
operator|(
literal|" in file '"
operator|+
name|filename
operator|+
literal|"'"
operator|)
else|:
literal|""
operator|)
operator|+
literal|": "
operator|+
name|version
operator|+
literal|" (needs to be between "
operator|+
name|minVersion
operator|+
literal|" and "
operator|+
name|maxVersion
operator|+
literal|"). This version of Lucene only supports indexes created with release 3.0 and later."
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

