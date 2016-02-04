begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
package|;
end_package

begin_comment
comment|/** Lucene's package information, including version. **/
end_comment

begin_class
DECL|class|LucenePackage
specifier|public
specifier|final
class|class
name|LucenePackage
block|{
DECL|method|LucenePackage
specifier|private
name|LucenePackage
parameter_list|()
block|{}
comment|// can't construct
comment|/** Return Lucene's package, including version information. */
DECL|method|get
specifier|public
specifier|static
name|Package
name|get
parameter_list|()
block|{
return|return
name|LucenePackage
operator|.
name|class
operator|.
name|getPackage
argument_list|()
return|;
block|}
block|}
end_class

end_unit

