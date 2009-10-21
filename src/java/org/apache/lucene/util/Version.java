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
comment|/**  * Use by certain classes to match version compatibility  * across releases of Lucene.  */
end_comment

begin_enum
DECL|enum|Version
specifier|public
enum|enum
name|Version
block|{
comment|/** Match settings and bugs in Lucene's 2.0 release. */
DECL|enum constant|LUCENE_20
name|LUCENE_20
block|,
comment|/** Match settings and bugs in Lucene's 2.1 release. */
DECL|enum constant|LUCENE_21
name|LUCENE_21
block|,
comment|/** Match settings and bugs in Lucene's 2.2 release. */
DECL|enum constant|LUCENE_22
name|LUCENE_22
block|,
comment|/** Match settings and bugs in Lucene's 2.3 release. */
DECL|enum constant|LUCENE_23
name|LUCENE_23
block|,
comment|/** Match settings and bugs in Lucene's 2.4 release. */
DECL|enum constant|LUCENE_24
name|LUCENE_24
block|,
comment|/** Match settings and bugs in Lucene's 2.9 release. */
DECL|enum constant|LUCENE_29
name|LUCENE_29
block|,
comment|/** Match settings and bugs in Lucene's 3.0 release. */
DECL|enum constant|LUCENE_30
name|LUCENE_30
block|,
comment|/* Add new constants for later versions **here** to respect order! */
comment|/** Use this to get the latest&amp; greatest settings, bug    *  fixes, etc, for Lucene.    *    *<p><b>WARNING</b>: if you use this setting, and then    * upgrade to a newer release of Lucene, sizable changes    * may happen.  If precise back compatibility is important    * then you should instead explicitly specify an actual    * version.    */
DECL|enum constant|LUCENE_CURRENT
name|LUCENE_CURRENT
block|;
DECL|method|onOrAfter
specifier|public
name|boolean
name|onOrAfter
parameter_list|(
name|Version
name|other
parameter_list|)
block|{
return|return
name|compareTo
argument_list|(
name|other
argument_list|)
operator|>=
literal|0
return|;
block|}
block|}
end_enum

end_unit

