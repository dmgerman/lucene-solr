begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Codec
import|;
end_import

begin_comment
comment|/**  * Tests with the default randomized codec. Not really redundant with  * other specific instantiations since we want to test some test-only impls  * like Asserting, as well as make it easy to write a codec and pass -Dtests.codec  */
end_comment

begin_class
DECL|class|TestTermVectorsFormat
specifier|public
class|class
name|TestTermVectorsFormat
extends|extends
name|BaseTermVectorsFormatTestCase
block|{
annotation|@
name|Override
DECL|method|getCodec
specifier|protected
name|Codec
name|getCodec
parameter_list|()
block|{
return|return
name|Codec
operator|.
name|getDefault
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|testMergeStability
specifier|public
name|void
name|testMergeStability
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"The MockRandom PF randomizes content on the fly, so we can't check it"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

