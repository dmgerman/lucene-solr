begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.lucene50
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene50
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
name|codecs
operator|.
name|NormsFormat
import|;
end_import

begin_comment
comment|/**  * Codec for testing 5.0 index format  * @deprecated Only for testing old 5.0-5.2 segments  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|Lucene50RWCodec
specifier|final
class|class
name|Lucene50RWCodec
extends|extends
name|Lucene50Codec
block|{
DECL|field|normsFormat
specifier|private
specifier|final
name|NormsFormat
name|normsFormat
init|=
operator|new
name|Lucene50RWNormsFormat
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|normsFormat
specifier|public
name|NormsFormat
name|normsFormat
parameter_list|()
block|{
return|return
name|normsFormat
return|;
block|}
block|}
end_class

end_unit

