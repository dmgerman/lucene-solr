begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Dictionary represented by a text file.  *   *<p/>Format allowed: 1 word per line:<br/>  * word1<br/>  * word2<br/>  * word3<br/>  */
end_comment

begin_class
DECL|class|PlainTextDictionary
specifier|public
class|class
name|PlainTextDictionary
implements|implements
name|Dictionary
block|{
DECL|field|in
specifier|private
name|BufferedReader
name|in
decl_stmt|;
DECL|field|line
specifier|private
name|String
name|line
decl_stmt|;
DECL|field|hasNextCalled
specifier|private
name|boolean
name|hasNextCalled
decl_stmt|;
DECL|method|PlainTextDictionary
specifier|public
name|PlainTextDictionary
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
name|in
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|PlainTextDictionary
specifier|public
name|PlainTextDictionary
parameter_list|(
name|InputStream
name|dictFile
parameter_list|)
block|{
name|in
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|dictFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a dictionary based on a reader.    */
DECL|method|PlainTextDictionary
specifier|public
name|PlainTextDictionary
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
name|in
operator|=
operator|new
name|BufferedReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
DECL|method|getWordsIterator
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getWordsIterator
parameter_list|()
block|{
return|return
operator|new
name|fileIterator
argument_list|()
return|;
block|}
DECL|class|fileIterator
specifier|final
class|class
name|fileIterator
implements|implements
name|Iterator
argument_list|<
name|String
argument_list|>
block|{
DECL|method|next
specifier|public
name|String
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNextCalled
condition|)
block|{
name|hasNext
argument_list|()
expr_stmt|;
block|}
name|hasNextCalled
operator|=
literal|false
expr_stmt|;
return|return
name|line
return|;
block|}
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
name|hasNextCalled
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|line
operator|=
name|in
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
return|return
operator|(
name|line
operator|!=
literal|null
operator|)
condition|?
literal|true
else|:
literal|false
return|;
block|}
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

