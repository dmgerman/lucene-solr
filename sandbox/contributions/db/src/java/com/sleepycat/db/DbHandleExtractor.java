begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|com.sleepycat.db
package|package
name|com
operator|.
name|sleepycat
operator|.
name|db
package|;
end_package

begin_comment
comment|/**  * Copyright 2002-2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|internal
operator|.
name|Db
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|db
operator|.
name|internal
operator|.
name|DbTxn
import|;
end_import

begin_comment
comment|/**  * This class is a hack to workaround the need to rewrite the entire  * org.apache.lucene.store.db package after Sleepycat radically changed its  * Java API from version 4.2.52 to version 4.3.21.  *   * The code below extracts the package-accessible internal handle instances  * that were the entrypoint objects in the pre-4.3 Java API and that wrap the  * actual Berkeley DB C objects via SWIG.  *  * @author Andi Vajda  */
end_comment

begin_class
DECL|class|DbHandleExtractor
specifier|public
class|class
name|DbHandleExtractor
block|{
DECL|method|DbHandleExtractor
specifier|private
name|DbHandleExtractor
parameter_list|()
block|{     }
DECL|method|getDb
specifier|static
specifier|public
name|Db
name|getDb
parameter_list|(
name|Database
name|database
parameter_list|)
block|{
return|return
name|database
operator|.
name|db
return|;
block|}
DECL|method|getDbTxn
specifier|static
specifier|public
name|DbTxn
name|getDbTxn
parameter_list|(
name|Transaction
name|transaction
parameter_list|)
block|{
return|return
name|transaction
operator|.
name|txn
return|;
block|}
block|}
end_class

end_unit

