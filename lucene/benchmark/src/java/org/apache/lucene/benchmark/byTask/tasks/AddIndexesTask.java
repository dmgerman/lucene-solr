begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
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
name|DirectoryReader
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
name|LeafReader
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
name|LeafReaderContext
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|FSDirectory
import|;
end_import

begin_comment
comment|/**  * Adds an input index to an existing index, using  * {@link IndexWriter#addIndexes(Directory...)} or  * {@link IndexWriter#addIndexes(LeafReader...)}. The location of the input  * index is specified by the parameter {@link #ADDINDEXES_INPUT_DIR} and is  * assumed to be a directory on the file system.  *<p>  * Takes optional parameter {@code useAddIndexesDir} which specifies which  * addIndexes variant to use (defaults to true, to use addIndexes(Directory)).  */
end_comment

begin_class
DECL|class|AddIndexesTask
specifier|public
class|class
name|AddIndexesTask
extends|extends
name|PerfTask
block|{
DECL|field|ADDINDEXES_INPUT_DIR
specifier|public
specifier|static
specifier|final
name|String
name|ADDINDEXES_INPUT_DIR
init|=
literal|"addindexes.input.dir"
decl_stmt|;
DECL|method|AddIndexesTask
specifier|public
name|AddIndexesTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
block|}
DECL|field|useAddIndexesDir
specifier|private
name|boolean
name|useAddIndexesDir
init|=
literal|true
decl_stmt|;
DECL|field|inputDir
specifier|private
name|FSDirectory
name|inputDir
decl_stmt|;
annotation|@
name|Override
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
name|String
name|inputDirProp
init|=
name|getRunData
argument_list|()
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
name|ADDINDEXES_INPUT_DIR
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|inputDirProp
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"config parameter "
operator|+
name|ADDINDEXES_INPUT_DIR
operator|+
literal|" not specified in configuration"
argument_list|)
throw|;
block|}
name|inputDir
operator|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|inputDirProp
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexWriter
name|writer
init|=
name|getRunData
argument_list|()
operator|.
name|getIndexWriter
argument_list|()
decl_stmt|;
if|if
condition|(
name|useAddIndexesDir
condition|)
block|{
name|writer
operator|.
name|addIndexes
argument_list|(
name|inputDir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
init|(
name|IndexReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|inputDir
argument_list|)
init|)
block|{
name|LeafReader
name|leaves
index|[]
init|=
operator|new
name|LeafReader
index|[
name|r
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|LeafReaderContext
name|leaf
range|:
name|r
operator|.
name|leaves
argument_list|()
control|)
block|{
name|leaves
index|[
name|i
operator|++
index|]
operator|=
name|leaf
operator|.
name|reader
argument_list|()
expr_stmt|;
block|}
name|writer
operator|.
name|addIndexes
argument_list|(
name|leaves
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|1
return|;
block|}
comment|/**    * Set the params (useAddIndexesDir only)    *     * @param params    *          {@code useAddIndexesDir=true} for using    *          {@link IndexWriter#addIndexes(Directory...)} or {@code false} for    *          using {@link IndexWriter#addIndexes(LeafReader...)}. Defaults to    *          {@code true}.    */
annotation|@
name|Override
DECL|method|setParams
specifier|public
name|void
name|setParams
parameter_list|(
name|String
name|params
parameter_list|)
block|{
name|super
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|useAddIndexesDir
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|supportsParams
specifier|public
name|boolean
name|supportsParams
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|inputDir
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

