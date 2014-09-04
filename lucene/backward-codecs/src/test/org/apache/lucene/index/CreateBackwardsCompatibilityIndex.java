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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|// This class exists only so it has a name that the junit runner will not pickup,
end_comment

begin_comment
comment|// so these index creation "tests" can only be run explicitly
end_comment

begin_class
DECL|class|CreateBackwardsCompatibilityIndex
specifier|public
class|class
name|CreateBackwardsCompatibilityIndex
extends|extends
name|TestBackwardsCompatibility
block|{
comment|// These indexes will be created under directory /tmp/idx/.
comment|//
comment|// Be sure to create the indexes with the actual format:
comment|//  ant test -Dtestcase=TestBackwardsCompatibility -Dversion=x.y.z -Dtests.codec=LuceneXY -Dtests.useSecurityManager=false
comment|//
comment|// Zip up the generated indexes:
comment|//
comment|//    cd /tmp/idx/index.cfs   ; zip index.<VERSION>.cfs.zip *
comment|//    cd /tmp/idx/index.nocfs ; zip index.<VERSION>.nocfs.zip *
comment|//
comment|// Then move those 2 zip files to your trunk checkout and add them
comment|// to the oldNames array.
DECL|method|testCreateCFS
specifier|public
name|void
name|testCreateCFS
parameter_list|()
throws|throws
name|IOException
block|{
name|createIndex
argument_list|(
literal|"index.cfs"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testCreateNonCFS
specifier|public
name|void
name|testCreateNonCFS
parameter_list|()
throws|throws
name|IOException
block|{
name|createIndex
argument_list|(
literal|"index.nocfs"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// These are only needed for the special upgrade test to verify
comment|// that also single-segment indexes are correctly upgraded by IndexUpgrader.
comment|// You don't need them to be build for non-4.0 (the test is happy with just one
comment|// "old" segment format, version is unimportant:
DECL|method|testCreateSingleSegmentCFS
specifier|public
name|void
name|testCreateSingleSegmentCFS
parameter_list|()
throws|throws
name|IOException
block|{
name|createIndex
argument_list|(
literal|"index.singlesegment.cfs"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testCreateSingleSegmentNoCFS
specifier|public
name|void
name|testCreateSingleSegmentNoCFS
parameter_list|()
throws|throws
name|IOException
block|{
name|createIndex
argument_list|(
literal|"index.singlesegment.nocfs"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

