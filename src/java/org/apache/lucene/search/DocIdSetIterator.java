begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * This abstract class defines methods to iterate over a set of non-decreasing  * doc ids. Note that this class assumes it iterates on doc Ids, and therefore  * {@link #NO_MORE_DOCS} is set to {@value #NO_MORE_DOCS} in order to be used as  * a sentinel object. Implementations of this class are expected to consider  * {@link Integer#MAX_VALUE} as an invalid value.  */
end_comment

begin_class
DECL|class|DocIdSetIterator
specifier|public
specifier|abstract
class|class
name|DocIdSetIterator
block|{
comment|// TODO (3.0): review the javadocs and remove any references to '3.0'.
DECL|field|doc
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
comment|/**    * When returned by {@link #nextDoc()}, {@link #advance(int)} and    * {@link #doc()} it means there are no more docs in the iterator.    */
DECL|field|NO_MORE_DOCS
specifier|public
specifier|static
specifier|final
name|int
name|NO_MORE_DOCS
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
comment|/**    * Unsupported anymore. Call {@link #docID()} instead. This method throws    * {@link UnsupportedOperationException} if called.    *     * @deprecated use {@link #docID()} instead.    */
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Call docID() instead."
argument_list|)
throw|;
block|}
comment|/**    * Returns the following:    *<ul>    *<li>-1 or {@link #NO_MORE_DOCS} if {@link #nextDoc()} or    * {@link #advance(int)} were not called yet.    *<li>{@link #NO_MORE_DOCS} if the iterator has exhausted.    *<li>Otherwise it should return the doc ID it is currently on.    *</ul>    *<p>    *<b>NOTE:</b> in 3.0, this method will become abstract.    *     * @since 2.9    */
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
comment|/**    * Unsupported anymore. Call {@link #nextDoc()} instead. This method throws    * {@link UnsupportedOperationException} if called.    *     * @deprecated use {@link #nextDoc()} instead. This will be removed in 3.0    */
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Call nextDoc() instead."
argument_list|)
throw|;
block|}
comment|/**    * Unsupported anymore. Call {@link #advance(int)} instead. This method throws    * {@link UnsupportedOperationException} if called.    *     * @deprecated use {@link #advance(int)} instead. This will be removed in 3.0    */
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Call advance() instead."
argument_list|)
throw|;
block|}
comment|/**    * Advances to the next document in the set and returns the doc it is    * currently on, or {@link #NO_MORE_DOCS} if there are no more docs in the    * set.<br>    *     *<b>NOTE:</b> in 3.0 this method will become abstract, following the removal    * of {@link #next()}. For backward compatibility it is implemented as:    *     *<pre>    * public int nextDoc() throws IOException {    *   return next() ? doc() : NO_MORE_DOCS;    * }    *</pre>    *     *<b>NOTE:</b> after the iterator has exhausted you should not call this    * method, as it may result in unpredicted behavior.    *     * @since 2.9    */
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|doc
operator|=
name|next
argument_list|()
condition|?
name|doc
argument_list|()
else|:
name|NO_MORE_DOCS
return|;
block|}
comment|/**    * Advances to the first beyond the current whose document number is greater    * than or equal to<i>target</i>. Returns the current document number or    * {@link #NO_MORE_DOCS} if there are no more docs in the set.    *<p>    * Behaves as if written:    *     *<pre>    * int advance(int target) {    *   int doc;    *   while ((doc = nextDoc())&lt; target) {    *   }    *   return doc;    * }    *</pre>    *     * Some implementations are considerably more efficient than that.    *<p>    *<b>NOTE:</b> certain implemenations may return a different value (each    * time) if called several times in a row with the same target.    *<p>    *<b>NOTE:</b> this method may be called with {@value #NO_MORE_DOCS} for    * efficiency by some Scorers. If your implementation cannot efficiently    * determine that it should exhaust, it is recommended that you check for that    * value in each call to this method.    *<p>    *<b>NOTE:</b> after the iterator has exhausted you should not call this    * method, as it may result in unpredicted behavior.    *<p>    *<b>NOTE:</b> in 3.0 this method will become abstract, following the removal    * of {@link #skipTo(int)}.    *     * @since 2.9    */
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|nextDoc
argument_list|()
operator|<
name|target
condition|)
block|{}
return|return
name|doc
return|;
block|}
block|}
end_class

end_unit

