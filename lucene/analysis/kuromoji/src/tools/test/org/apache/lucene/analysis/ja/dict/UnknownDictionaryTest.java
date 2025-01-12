begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.ja.dict
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
operator|.
name|dict
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
name|analysis
operator|.
name|ja
operator|.
name|util
operator|.
name|CSVUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
operator|.
name|util
operator|.
name|UnknownDictionaryWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|UnknownDictionaryTest
specifier|public
class|class
name|UnknownDictionaryTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|FILENAME
specifier|public
specifier|static
specifier|final
name|String
name|FILENAME
init|=
literal|"unk-tokeninfo-dict.obj"
decl_stmt|;
annotation|@
name|Test
DECL|method|testPutCharacterCategory
specifier|public
name|void
name|testPutCharacterCategory
parameter_list|()
block|{
name|UnknownDictionaryWriter
name|unkDic
init|=
operator|new
name|UnknownDictionaryWriter
argument_list|(
literal|10
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
decl_stmt|;
try|try
block|{
name|unkDic
operator|.
name|putCharacterCategory
argument_list|(
literal|0
argument_list|,
literal|"DUMMY_NAME"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{            }
try|try
block|{
name|unkDic
operator|.
name|putCharacterCategory
argument_list|(
operator|-
literal|1
argument_list|,
literal|"KATAKANA"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{            }
name|unkDic
operator|.
name|putCharacterCategory
argument_list|(
literal|0
argument_list|,
literal|"DEFAULT"
argument_list|)
expr_stmt|;
name|unkDic
operator|.
name|putCharacterCategory
argument_list|(
literal|1
argument_list|,
literal|"GREEK"
argument_list|)
expr_stmt|;
name|unkDic
operator|.
name|putCharacterCategory
argument_list|(
literal|2
argument_list|,
literal|"HIRAGANA"
argument_list|)
expr_stmt|;
name|unkDic
operator|.
name|putCharacterCategory
argument_list|(
literal|3
argument_list|,
literal|"KATAKANA"
argument_list|)
expr_stmt|;
name|unkDic
operator|.
name|putCharacterCategory
argument_list|(
literal|4
argument_list|,
literal|"KANJI"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPut
specifier|public
name|void
name|testPut
parameter_list|()
block|{
name|UnknownDictionaryWriter
name|unkDic
init|=
operator|new
name|UnknownDictionaryWriter
argument_list|(
literal|10
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
decl_stmt|;
try|try
block|{
name|unkDic
operator|.
name|put
argument_list|(
name|CSVUtil
operator|.
name|parse
argument_list|(
literal|"KANJI,1285,11426,åè©,ä¸è¬,*,*,*,*,*,*,*"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{            }
name|String
name|entry1
init|=
literal|"ALPHA,1285,1285,13398,åè©,ä¸è¬,*,*,*,*,*,*,*"
decl_stmt|;
name|String
name|entry2
init|=
literal|"HIRAGANA,1285,1285,13069,åè©,ä¸è¬,*,*,*,*,*,*,*"
decl_stmt|;
name|String
name|entry3
init|=
literal|"KANJI,1285,1285,11426,åè©,ä¸è¬,*,*,*,*,*,*,*"
decl_stmt|;
name|unkDic
operator|.
name|putCharacterCategory
argument_list|(
literal|0
argument_list|,
literal|"ALPHA"
argument_list|)
expr_stmt|;
name|unkDic
operator|.
name|putCharacterCategory
argument_list|(
literal|1
argument_list|,
literal|"HIRAGANA"
argument_list|)
expr_stmt|;
name|unkDic
operator|.
name|putCharacterCategory
argument_list|(
literal|2
argument_list|,
literal|"KANJI"
argument_list|)
expr_stmt|;
name|unkDic
operator|.
name|put
argument_list|(
name|CSVUtil
operator|.
name|parse
argument_list|(
name|entry1
argument_list|)
argument_list|)
expr_stmt|;
name|unkDic
operator|.
name|put
argument_list|(
name|CSVUtil
operator|.
name|parse
argument_list|(
name|entry2
argument_list|)
argument_list|)
expr_stmt|;
name|unkDic
operator|.
name|put
argument_list|(
name|CSVUtil
operator|.
name|parse
argument_list|(
name|entry3
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

