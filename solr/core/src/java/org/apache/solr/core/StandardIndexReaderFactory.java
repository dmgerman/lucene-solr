begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
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
name|store
operator|.
name|Directory
import|;
end_import

begin_comment
comment|/**  * Default IndexReaderFactory implementation. Returns a standard Lucene  * IndexReader.  *   * @see IndexReader#open(Directory)  */
end_comment

begin_class
DECL|class|StandardIndexReaderFactory
specifier|public
class|class
name|StandardIndexReaderFactory
extends|extends
name|IndexReaderFactory
block|{
annotation|@
name|Override
DECL|method|newReader
specifier|public
name|IndexReader
name|newReader
parameter_list|(
name|Directory
name|indexDir
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|IndexReader
operator|.
name|open
argument_list|(
name|indexDir
argument_list|,
name|termInfosIndexDivisor
argument_list|)
return|;
block|}
block|}
end_class

end_unit

