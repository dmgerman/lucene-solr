begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/*  * Some of this code came from the excellent Unicode  * conversion examples from:  *  *   http://www.unicode.org/Public/PROGRAMS/CVTUTF  *  * Full Copyright for that code follows: */
end_comment

begin_comment
comment|/*  * Copyright 2001-2004 Unicode, Inc.  *   * Disclaimer  *   * This source code is provided as is by Unicode, Inc. No claims are  * made as to fitness for any particular purpose. No warranties of any  * kind are expressed or implied. The recipient agrees to determine  * applicability of information provided. If this file has been  * purchased on magnetic or optical media from Unicode, Inc., the  * sole remedy for any claim will be exchange of defective media  * within 90 days of receipt.  *   * Limitations on Rights to Redistribute This Code  *   * Unicode, Inc. hereby grants the right to freely use the information  * supplied in this file in the creation of products supporting the  * Unicode Standard, and to make copies of this file in any form  * for internal or external distribution as long as this notice  * remains attached.  */
end_comment

begin_class
DECL|class|TestUnicodeUtil
specifier|public
class|class
name|TestUnicodeUtil
extends|extends
name|LuceneTestCase
block|{
DECL|method|testNextValidUTF16String
specifier|public
name|void
name|testNextValidUTF16String
parameter_list|()
block|{
comment|// valid UTF-16
name|assertEquals
argument_list|(
literal|"dogs"
argument_list|,
name|UnicodeUtil
operator|.
name|nextValidUTF16String
argument_list|(
literal|"dogs"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"dogs\uD802\uDC02"
argument_list|,
name|UnicodeUtil
operator|.
name|nextValidUTF16String
argument_list|(
literal|"dogs\uD802\uDC02"
argument_list|)
argument_list|)
expr_stmt|;
comment|// an illegal combination, where we have not yet enumerated into the supp
comment|// plane so we increment to H + \uDC00 (the lowest possible trail surrogate)
name|assertEquals
argument_list|(
literal|"dogs\uD801\uDC00"
argument_list|,
name|UnicodeUtil
operator|.
name|nextValidUTF16String
argument_list|(
literal|"dogs\uD801"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"dogs\uD801\uDC00"
argument_list|,
name|UnicodeUtil
operator|.
name|nextValidUTF16String
argument_list|(
literal|"dogs\uD801b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"dogs\uD801\uDC00"
argument_list|,
name|UnicodeUtil
operator|.
name|nextValidUTF16String
argument_list|(
literal|"dogs\uD801\uD800"
argument_list|)
argument_list|)
expr_stmt|;
comment|// an illegal combination where we have already enumerated the trail
comment|// we must increment the lead and start the trail back at the beginning.
name|assertEquals
argument_list|(
literal|"dogs\uD802\uDC00"
argument_list|,
name|UnicodeUtil
operator|.
name|nextValidUTF16String
argument_list|(
literal|"dogs\uD801\uE001"
argument_list|)
argument_list|)
expr_stmt|;
comment|// an illegal combination where we have exhausted the supp plane
comment|// we must now move to the lower bmp.
name|assertEquals
argument_list|(
literal|"dogs\uE000"
argument_list|,
name|UnicodeUtil
operator|.
name|nextValidUTF16String
argument_list|(
literal|"dogs\uDBFF\uE001"
argument_list|)
argument_list|)
expr_stmt|;
comment|// an unpaired trail surrogate. this is invalid when not preceded by a lead
comment|// surrogate. in this case we have to bump to \uE000 (the lowest possible
comment|// "upper BMP")
name|assertEquals
argument_list|(
literal|"dogs\uE000"
argument_list|,
name|UnicodeUtil
operator|.
name|nextValidUTF16String
argument_list|(
literal|"dogs\uDC00"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\uE000"
argument_list|,
name|UnicodeUtil
operator|.
name|nextValidUTF16String
argument_list|(
literal|"\uDC00dogs"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

