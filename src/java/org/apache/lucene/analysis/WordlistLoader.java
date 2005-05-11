begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|FileReader
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
name|java
operator|.
name|io
operator|.
name|LineNumberReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Loader for text files that represent a list of stopwords.  *  * @author Gerhard Schwarz  * @version $Id$  */
end_comment

begin_class
DECL|class|WordlistLoader
specifier|public
class|class
name|WordlistLoader
block|{
comment|/**    * Loads a text file and adds every line as an entry to a HashSet (omitting    * leading and trailing whitespace). Every line of the file should contain only     * one word. The words need to be in lowercase if you make use of an    * Analyzer which uses LowerCaseFilter (like GermanAnalyzer).    *     * @param wordfile File containing the wordlist    * @return A HashSet with the file's words    */
DECL|method|getWordSet
specifier|public
specifier|static
name|HashSet
name|getWordSet
parameter_list|(
name|File
name|wordfile
parameter_list|)
throws|throws
name|IOException
block|{
name|HashSet
name|result
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|FileReader
name|freader
init|=
literal|null
decl_stmt|;
name|LineNumberReader
name|lnr
init|=
literal|null
decl_stmt|;
try|try
block|{
name|freader
operator|=
operator|new
name|FileReader
argument_list|(
name|wordfile
argument_list|)
expr_stmt|;
name|lnr
operator|=
operator|new
name|LineNumberReader
argument_list|(
name|freader
argument_list|)
expr_stmt|;
name|String
name|word
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|word
operator|=
name|lnr
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|word
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|lnr
operator|!=
literal|null
condition|)
name|lnr
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|freader
operator|!=
literal|null
condition|)
name|freader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * @param path      Path to the wordlist    * @param wordfile  Name of the wordlist    *     * @deprecated Use {@link #getWordSet(File)} instead    */
DECL|method|getWordtable
specifier|public
specifier|static
name|Hashtable
name|getWordtable
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|wordfile
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getWordtable
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|,
name|wordfile
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * @param wordfile  Complete path to the wordlist    *     * @deprecated Use {@link #getWordSet(File)} instead    */
DECL|method|getWordtable
specifier|public
specifier|static
name|Hashtable
name|getWordtable
parameter_list|(
name|String
name|wordfile
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getWordtable
argument_list|(
operator|new
name|File
argument_list|(
name|wordfile
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * @param wordfile  File object that points to the wordlist    *    * @deprecated Use {@link #getWordSet(File)} instead    */
DECL|method|getWordtable
specifier|public
specifier|static
name|Hashtable
name|getWordtable
parameter_list|(
name|File
name|wordfile
parameter_list|)
throws|throws
name|IOException
block|{
name|HashSet
name|wordSet
init|=
operator|(
name|HashSet
operator|)
name|getWordSet
argument_list|(
name|wordfile
argument_list|)
decl_stmt|;
name|Hashtable
name|result
init|=
name|makeWordTable
argument_list|(
name|wordSet
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
comment|/**    * Builds a wordlist table, using words as both keys and values    * for backward compatibility.    *    * @param wordSet   stopword set    */
DECL|method|makeWordTable
specifier|private
specifier|static
name|Hashtable
name|makeWordTable
parameter_list|(
name|HashSet
name|wordSet
parameter_list|)
block|{
name|Hashtable
name|table
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|wordSet
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|word
init|=
operator|(
name|String
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|table
operator|.
name|put
argument_list|(
name|word
argument_list|,
name|word
argument_list|)
expr_stmt|;
block|}
return|return
name|table
return|;
block|}
block|}
end_class

end_unit

