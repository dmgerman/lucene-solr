begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.demo
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|demo
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
name|Term
import|;
end_import

begin_comment
comment|//import org.apache.lucene.index.Term;
end_comment

begin_comment
comment|/** Deletes documents from an index that do not contain a term. */
end_comment

begin_class
DECL|class|DeleteFiles
specifier|public
class|class
name|DeleteFiles
block|{
DECL|method|DeleteFiles
specifier|private
name|DeleteFiles
parameter_list|()
block|{}
comment|// singleton
comment|/** Deletes documents from an index that do not contain a term. */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|String
name|usage
init|=
literal|"java org.apache.lucene.demo.DeleteFiles<unique_term>"
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: "
operator|+
name|usage
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Directory
name|directory
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
literal|"index"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
literal|"path"
argument_list|,
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|int
name|deleted
init|=
name|reader
operator|.
name|deleteDocuments
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"deleted "
operator|+
name|deleted
operator|+
literal|" documents containing "
operator|+
name|term
argument_list|)
expr_stmt|;
comment|// one can also delete documents by their internal id:
comment|/*       for (int i = 0; i< reader.maxDoc(); i++) {         System.out.println("Deleting document with id " + i);         reader.delete(i);       }*/
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" caught a "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|+
literal|"\n with message: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

