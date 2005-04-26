begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

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
name|document
operator|.
name|Document
import|;
end_import

begin_comment
comment|/**  * Wrapper used by {@link HitIterator} to provide a lazily loaded hit  * from {@link Hits}.  *  * @author Jeremy Rayner  */
end_comment

begin_class
DECL|class|Hit
specifier|public
class|class
name|Hit
implements|implements
name|java
operator|.
name|io
operator|.
name|Serializable
block|{
DECL|field|score
specifier|private
name|float
name|score
decl_stmt|;
DECL|field|id
specifier|private
name|int
name|id
decl_stmt|;
DECL|field|doc
specifier|private
name|Document
name|doc
init|=
literal|null
decl_stmt|;
DECL|field|resolved
specifier|private
name|boolean
name|resolved
init|=
literal|false
decl_stmt|;
DECL|field|hits
specifier|private
name|Hits
name|hits
init|=
literal|null
decl_stmt|;
DECL|field|hitNumber
specifier|private
name|int
name|hitNumber
decl_stmt|;
comment|/**    * Constructed from {@link HitIterator}    * @param hits Hits returned from a search    * @param hitNumber Hit index in Hits    */
DECL|method|Hit
name|Hit
parameter_list|(
name|Hits
name|hits
parameter_list|,
name|int
name|hitNumber
parameter_list|)
block|{
name|this
operator|.
name|hits
operator|=
name|hits
expr_stmt|;
name|this
operator|.
name|hitNumber
operator|=
name|hitNumber
expr_stmt|;
block|}
comment|/**    * Returns document for this hit.    *    * @see Hits#doc(int)    */
DECL|method|getDocument
specifier|public
name|Document
name|getDocument
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|resolved
condition|)
name|fetchTheHit
argument_list|()
expr_stmt|;
return|return
name|doc
return|;
block|}
comment|/**    * Returns score for this hit.    *    * @see Hits#score(int)    */
DECL|method|getScore
specifier|public
name|float
name|getScore
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|resolved
condition|)
name|fetchTheHit
argument_list|()
expr_stmt|;
return|return
name|score
return|;
block|}
comment|/**    * Returns id for this hit.    *    * @see Hits#id(int)    */
DECL|method|getId
specifier|public
name|int
name|getId
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|resolved
condition|)
name|fetchTheHit
argument_list|()
expr_stmt|;
return|return
name|id
return|;
block|}
DECL|method|fetchTheHit
specifier|private
name|void
name|fetchTheHit
parameter_list|()
throws|throws
name|IOException
block|{
name|doc
operator|=
name|hits
operator|.
name|doc
argument_list|(
name|hitNumber
argument_list|)
expr_stmt|;
name|score
operator|=
name|hits
operator|.
name|score
argument_list|(
name|hitNumber
argument_list|)
expr_stmt|;
name|id
operator|=
name|hits
operator|.
name|id
argument_list|(
name|hitNumber
argument_list|)
expr_stmt|;
name|resolved
operator|=
literal|true
expr_stmt|;
block|}
comment|// provide some of the Document style interface (the simple stuff)
comment|/**    * Returns the boost factor for this hit on any field of the underlying document.    *    * @see Document#getBoost()    */
DECL|method|getBoost
specifier|public
name|float
name|getBoost
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getDocument
argument_list|()
operator|.
name|getBoost
argument_list|()
return|;
block|}
comment|/**    * Returns the string value of the field with the given name if any exist in    * this document, or null.  If multiple fields exist with this name, this    * method returns the first value added. If only binary fields with this name    * exist, returns null.    *    * @see Document#get(String)    */
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getDocument
argument_list|()
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**    * Prints the fields of the underlying document for human consumption.    *<p/>    * If an IOException occurs whilst getting the document, returns null    *    * @see Document#toString()    */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
try|try
block|{
return|return
name|getDocument
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

