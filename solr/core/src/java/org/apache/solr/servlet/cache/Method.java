begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.servlet.cache
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|servlet
operator|.
name|cache
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_enum
DECL|enum|Method
specifier|public
enum|enum
name|Method
block|{
DECL|enum constant|GET
DECL|enum constant|POST
DECL|enum constant|HEAD
DECL|enum constant|OTHER
name|GET
block|,
name|POST
block|,
name|HEAD
block|,
name|OTHER
block|;
DECL|method|getMethod
specifier|public
specifier|static
name|Method
name|getMethod
parameter_list|(
name|String
name|method
parameter_list|)
block|{
try|try
block|{
return|return
name|Method
operator|.
name|valueOf
argument_list|(
name|method
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
name|OTHER
return|;
block|}
block|}
block|}
end_enum

end_unit

