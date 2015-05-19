begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|DocIdSetIterator
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
name|TwoPhaseIterator
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

begin_comment
comment|/** Iterates through combinations of start/end positions per-doc.  *  Each start/end position represents a range of term positions within the current document.  *  These are enumerated in order, by increasing document number, within that by  *  increasing start position and finally by increasing end position.  */
end_comment

begin_class
DECL|class|Spans
specifier|public
specifier|abstract
class|class
name|Spans
extends|extends
name|DocIdSetIterator
block|{
DECL|field|NO_MORE_POSITIONS
specifier|public
specifier|static
specifier|final
name|int
name|NO_MORE_POSITIONS
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
comment|/**    * Returns the next start position for the current doc.    * There is always at least one start/end position per doc.    * After the last start/end position at the current doc this returns {@link #NO_MORE_POSITIONS}.    */
DECL|method|nextStartPosition
specifier|public
specifier|abstract
name|int
name|nextStartPosition
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the start position in the current doc, or -1 when {@link #nextStartPosition} was not yet called on the current doc.    * After the last start/end position at the current doc this returns {@link #NO_MORE_POSITIONS}.    */
DECL|method|startPosition
specifier|public
specifier|abstract
name|int
name|startPosition
parameter_list|()
function_decl|;
comment|/**    * Returns the end position for the current start position, or -1 when {@link #nextStartPosition} was not yet called on the current doc.    * After the last start/end position at the current doc this returns {@link #NO_MORE_POSITIONS}.    */
DECL|method|endPosition
specifier|public
specifier|abstract
name|int
name|endPosition
parameter_list|()
function_decl|;
comment|/**    * Collect data from the current Spans    * @param collector a SpanCollector    *    * @lucene.experimental    */
DECL|method|collect
specifier|public
specifier|abstract
name|void
name|collect
parameter_list|(
name|SpanCollector
name|collector
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Optional method: Return a {@link TwoPhaseIterator} view of this    * {@link Spans}. A return value of {@code null} indicates that    * two-phase iteration is not supported.    *    * Note that the returned {@link TwoPhaseIterator}'s    * {@link TwoPhaseIterator#approximation() approximation} must    * advance documents synchronously with this iterator:    * advancing the approximation must    * advance this iterator and vice-versa.    *    * Implementing this method is typically useful on a {@link Spans}    * that has a high per-document overhead for confirming matches.    *    * The default implementation returns {@code null}.    */
DECL|method|asTwoPhaseIterator
specifier|public
name|TwoPhaseIterator
name|asTwoPhaseIterator
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|Spans
argument_list|>
name|clazz
init|=
name|getClass
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|clazz
operator|.
name|isAnonymousClass
argument_list|()
condition|?
name|clazz
operator|.
name|getName
argument_list|()
else|:
name|clazz
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"(doc="
argument_list|)
operator|.
name|append
argument_list|(
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",start="
argument_list|)
operator|.
name|append
argument_list|(
name|startPosition
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",end="
argument_list|)
operator|.
name|append
argument_list|(
name|endPosition
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

