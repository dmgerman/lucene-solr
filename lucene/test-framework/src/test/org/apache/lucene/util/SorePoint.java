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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A pointcut-like definition where we should trigger  * an assumption or error.  */
end_comment

begin_enum
DECL|enum|SorePoint
specifier|public
enum|enum
name|SorePoint
block|{
comment|// STATIC_INITIALIZER, // I assume this will result in JUnit failure to load a suite.
DECL|enum constant|BEFORE_CLASS
name|BEFORE_CLASS
block|,
DECL|enum constant|INITIALIZER
name|INITIALIZER
block|,
DECL|enum constant|RULE
name|RULE
block|,
DECL|enum constant|BEFORE
name|BEFORE
block|,
DECL|enum constant|TEST
name|TEST
block|,
DECL|enum constant|AFTER
name|AFTER
block|,
DECL|enum constant|AFTER_CLASS
name|AFTER_CLASS
block|}
end_enum

end_unit

