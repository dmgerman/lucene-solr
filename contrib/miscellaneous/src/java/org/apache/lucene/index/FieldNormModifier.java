begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|Date
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
name|search
operator|.
name|Similarity
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
comment|/**  * Given a directory and a list of fields, updates the fieldNorms in place for every document.  *   * If Similarity class is specified, uses its lengthNorm method to set norms.  * If -n command line argument is used, removed field norms, as if   * {@link org.apache.lucene.document.Field.Index}.NO_NORMS was used.  *  *<p>  * NOTE: This will overwrite any length normalization or field/document boosts.  *</p>  *  */
end_comment

begin_class
DECL|class|FieldNormModifier
specifier|public
class|class
name|FieldNormModifier
block|{
comment|/**    * Command Line Execution method.    *    *<pre>    * Usage: FieldNormModifier /path/index&lt;package.SimilarityClassName | -n&gt; field1 field2 ...    *</pre>    */
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
throws|throws
name|IOException
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|3
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: FieldNormModifier<index><package.SimilarityClassName | -n><field1> [field2] ..."
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
name|Similarity
name|s
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|args
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
literal|"-n"
argument_list|)
condition|)
block|{
try|try
block|{
name|Class
name|simClass
init|=
name|Class
operator|.
name|forName
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|s
operator|=
operator|(
name|Similarity
operator|)
name|simClass
operator|.
name|newInstance
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
name|err
operator|.
name|println
argument_list|(
literal|"Couldn't instantiate similarity with empty constructor: "
operator|+
name|args
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
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
block|}
name|Directory
name|d
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|FieldNormModifier
name|fnm
init|=
operator|new
name|FieldNormModifier
argument_list|(
name|d
argument_list|,
name|s
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|2
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"Updating field: "
operator|+
name|args
index|[
name|i
index|]
operator|+
literal|" "
operator|+
operator|(
operator|new
name|Date
argument_list|()
operator|)
operator|.
name|toString
argument_list|()
operator|+
literal|" ... "
argument_list|)
expr_stmt|;
name|fnm
operator|.
name|reSetNorms
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
operator|new
name|Date
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
DECL|field|sim
specifier|private
name|Similarity
name|sim
decl_stmt|;
comment|/**    * Constructor for code that wishes to use this class programatically    * If Similarity is null, kill the field norms.    *    * @param d the Directory to modify    * @param s the Similiary to use (can be null)    */
DECL|method|FieldNormModifier
specifier|public
name|FieldNormModifier
parameter_list|(
name|Directory
name|d
parameter_list|,
name|Similarity
name|s
parameter_list|)
block|{
name|dir
operator|=
name|d
expr_stmt|;
name|sim
operator|=
name|s
expr_stmt|;
block|}
comment|/**    * Resets the norms for the specified field.    *    *<p>    * Opens a new IndexReader on the Directory given to this instance,    * modifies the norms (either using the Similarity given to this instance, or by using fake norms,    * and closes the IndexReader.    *</p>    *    * @param field the field whose norms should be reset    */
DECL|method|reSetNorms
specifier|public
name|void
name|reSetNorms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fieldName
init|=
name|field
operator|.
name|intern
argument_list|()
decl_stmt|;
name|int
index|[]
name|termCounts
init|=
operator|new
name|int
index|[
literal|0
index|]
decl_stmt|;
name|byte
index|[]
name|fakeNorms
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
name|IndexReader
name|reader
init|=
literal|null
decl_stmt|;
name|TermEnum
name|termEnum
init|=
literal|null
decl_stmt|;
name|TermDocs
name|termDocs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|termCounts
operator|=
operator|new
name|int
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
expr_stmt|;
comment|// if we are killing norms, get fake ones
if|if
condition|(
name|sim
operator|==
literal|null
condition|)
name|fakeNorms
operator|=
name|SegmentReader
operator|.
name|createFakeNorms
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|termEnum
operator|=
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|termDocs
operator|=
name|reader
operator|.
name|termDocs
argument_list|()
expr_stmt|;
do|do
block|{
name|Term
name|term
init|=
name|termEnum
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|!=
literal|null
operator|&&
name|term
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|termDocs
operator|.
name|seek
argument_list|(
name|termEnum
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|termDocs
operator|.
name|next
argument_list|()
condition|)
block|{
name|termCounts
index|[
name|termDocs
operator|.
name|doc
argument_list|()
index|]
operator|+=
name|termDocs
operator|.
name|freq
argument_list|()
expr_stmt|;
block|}
block|}
block|}
do|while
condition|(
name|termEnum
operator|.
name|next
argument_list|()
condition|)
do|;
block|}
finally|finally
block|{
if|if
condition|(
literal|null
operator|!=
name|termDocs
condition|)
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
literal|null
operator|!=
name|termEnum
condition|)
name|termEnum
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
literal|null
operator|!=
name|reader
condition|)
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|d
init|=
literal|0
init|;
name|d
operator|<
name|termCounts
operator|.
name|length
condition|;
name|d
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|reader
operator|.
name|isDeleted
argument_list|(
name|d
argument_list|)
condition|)
block|{
if|if
condition|(
name|sim
operator|==
literal|null
condition|)
name|reader
operator|.
name|setNorm
argument_list|(
name|d
argument_list|,
name|fieldName
argument_list|,
name|fakeNorms
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
else|else
name|reader
operator|.
name|setNorm
argument_list|(
name|d
argument_list|,
name|fieldName
argument_list|,
name|sim
operator|.
name|encodeNorm
argument_list|(
name|sim
operator|.
name|lengthNorm
argument_list|(
name|fieldName
argument_list|,
name|termCounts
index|[
name|d
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
literal|null
operator|!=
name|reader
condition|)
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

