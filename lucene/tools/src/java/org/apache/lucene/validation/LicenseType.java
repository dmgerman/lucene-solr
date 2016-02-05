begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.validation
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|validation
package|;
end_package

begin_comment
comment|/**  * A list of accepted licenses.  See also http://www.apache.org/legal/3party.html  *  **/
end_comment

begin_enum
DECL|enum|LicenseType
specifier|public
enum|enum
name|LicenseType
block|{
DECL|enum constant|ASL
name|ASL
argument_list|(
literal|"Apache Software License 2.0"
argument_list|,
literal|true
argument_list|)
block|,
DECL|enum constant|BSD
name|BSD
argument_list|(
literal|"Berkeley Software Distribution"
argument_list|,
literal|true
argument_list|)
block|,
DECL|enum constant|BSD_LIKE
name|BSD_LIKE
argument_list|(
literal|"BSD like license"
argument_list|,
literal|true
argument_list|)
block|,
comment|//BSD like just means someone has taken the BSD license and put in their name, copyright, or it's a very similar license.
DECL|enum constant|CDDL
name|CDDL
argument_list|(
literal|"Common Development and Distribution License"
argument_list|,
literal|false
argument_list|)
block|,
DECL|enum constant|CPL
name|CPL
argument_list|(
literal|"Common Public License"
argument_list|,
literal|true
argument_list|)
block|,
DECL|enum constant|EPL
name|EPL
argument_list|(
literal|"Eclipse Public License Version 1.0"
argument_list|,
literal|false
argument_list|)
block|,
DECL|enum constant|MIT
name|MIT
argument_list|(
literal|"Massachusetts Institute of Tech. License"
argument_list|,
literal|false
argument_list|)
block|,
DECL|enum constant|MPL
name|MPL
argument_list|(
literal|"Mozilla Public License"
argument_list|,
literal|false
argument_list|)
block|,
comment|//NOT SURE on the required notice
DECL|enum constant|PD
name|PD
argument_list|(
literal|"Public Domain"
argument_list|,
literal|false
argument_list|)
block|,
comment|//SUNBCLA("Sun Binary Code License Agreement"),
DECL|enum constant|SUN
name|SUN
argument_list|(
literal|"Sun Open Source License"
argument_list|,
literal|false
argument_list|)
block|,
DECL|enum constant|COMPOUND
name|COMPOUND
argument_list|(
literal|"Compound license (see NOTICE)."
argument_list|,
literal|true
argument_list|)
block|,
DECL|enum constant|FAKE
name|FAKE
argument_list|(
literal|"FAKE license - not needed"
argument_list|,
literal|false
argument_list|)
block|;
DECL|field|display
specifier|private
name|String
name|display
decl_stmt|;
DECL|field|noticeRequired
specifier|private
name|boolean
name|noticeRequired
decl_stmt|;
DECL|method|LicenseType
name|LicenseType
parameter_list|(
name|String
name|display
parameter_list|,
name|boolean
name|noticeRequired
parameter_list|)
block|{
name|this
operator|.
name|display
operator|=
name|display
expr_stmt|;
name|this
operator|.
name|noticeRequired
operator|=
name|noticeRequired
expr_stmt|;
block|}
DECL|method|isNoticeRequired
specifier|public
name|boolean
name|isNoticeRequired
parameter_list|()
block|{
return|return
name|noticeRequired
return|;
block|}
DECL|method|getDisplay
specifier|public
name|String
name|getDisplay
parameter_list|()
block|{
return|return
name|display
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"LicenseType{"
operator|+
literal|"display='"
operator|+
name|display
operator|+
literal|'\''
operator|+
literal|'}'
return|;
block|}
comment|/**    * Expected license file suffix for a given license type.    */
DECL|method|licenseFileSuffix
specifier|public
name|String
name|licenseFileSuffix
parameter_list|()
block|{
return|return
literal|"-LICENSE-"
operator|+
name|this
operator|.
name|name
argument_list|()
operator|+
literal|".txt"
return|;
block|}
comment|/**    * Expected notice file suffix for a given license type.    */
DECL|method|noticeFileSuffix
specifier|public
name|String
name|noticeFileSuffix
parameter_list|()
block|{
return|return
literal|"-NOTICE.txt"
return|;
block|}
block|}
end_enum

end_unit

