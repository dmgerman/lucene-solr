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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|IndexWriter
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
name|MergeScheduler
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
name|ConcurrentMergeScheduler
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
name|CheckIndex
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_class
DECL|class|_TestUtil
specifier|public
class|class
name|_TestUtil
block|{
comment|/** Returns temp dir, containing String arg in its name;    *  does not create the directory. */
DECL|method|getTempDir
specifier|public
specifier|static
name|File
name|getTempDir
parameter_list|(
name|String
name|desc
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|LuceneTestCaseJ4
operator|.
name|TEMP_DIR
argument_list|,
name|desc
operator|+
literal|"."
operator|+
operator|new
name|Random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
return|;
block|}
DECL|method|rmDir
specifier|public
specifier|static
name|void
name|rmDir
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|File
index|[]
name|files
init|=
name|dir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|files
index|[
name|i
index|]
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"could not delete "
operator|+
name|files
index|[
name|i
index|]
argument_list|)
throw|;
block|}
block|}
name|dir
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|rmDir
specifier|public
specifier|static
name|void
name|rmDir
parameter_list|(
name|String
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|rmDir
argument_list|(
operator|new
name|File
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|syncConcurrentMerges
specifier|public
specifier|static
name|void
name|syncConcurrentMerges
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
block|{
name|syncConcurrentMerges
argument_list|(
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|syncConcurrentMerges
specifier|public
specifier|static
name|void
name|syncConcurrentMerges
parameter_list|(
name|MergeScheduler
name|ms
parameter_list|)
block|{
if|if
condition|(
name|ms
operator|instanceof
name|ConcurrentMergeScheduler
condition|)
operator|(
operator|(
name|ConcurrentMergeScheduler
operator|)
name|ms
operator|)
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
comment|/** This runs the CheckIndex tool on the index in.  If any    *  issues are hit, a RuntimeException is thrown; else,    *  true is returned. */
DECL|method|checkIndex
specifier|public
specifier|static
name|boolean
name|checkIndex
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
name|CheckIndex
name|checker
init|=
operator|new
name|CheckIndex
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|checker
operator|.
name|setInfoStream
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|bos
argument_list|)
argument_list|)
expr_stmt|;
name|CheckIndex
operator|.
name|Status
name|indexStatus
init|=
name|checker
operator|.
name|checkIndex
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexStatus
operator|==
literal|null
operator|||
name|indexStatus
operator|.
name|clean
operator|==
literal|false
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"CheckIndex failed"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|bos
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"CheckIndex failed"
argument_list|)
throw|;
block|}
else|else
return|return
literal|true
return|;
block|}
comment|/** Use only for testing.    *  @deprecated -- in 3.0 we can use Arrays.toString    *  instead */
annotation|@
name|Deprecated
DECL|method|arrayToString
specifier|public
specifier|static
name|String
name|arrayToString
parameter_list|(
name|int
index|[]
name|array
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|array
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|array
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Use only for testing.    *  @deprecated -- in 3.0 we can use Arrays.toString    *  instead */
annotation|@
name|Deprecated
DECL|method|arrayToString
specifier|public
specifier|static
name|String
name|arrayToString
parameter_list|(
name|Object
index|[]
name|array
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|array
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|array
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

