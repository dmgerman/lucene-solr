begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  *<code>DocList</code> represents the result of a query: an ordered list of document ids with optional score.  * This list contains a subset of the complete list of documents actually matched:<code>size()</code>  * document ids starting at<code>offset()</code>.  *  *  * @since solr 0.9  */
end_comment

begin_interface
DECL|interface|DocList
specifier|public
interface|interface
name|DocList
extends|extends
name|DocSet
block|{
comment|/**    * Returns the zero based offset of this list within the total ordered list of matches to the query.    */
DECL|method|offset
specifier|public
name|int
name|offset
parameter_list|()
function_decl|;
comment|/**    * Returns the number of ids in this list.    */
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
function_decl|;
comment|/**    * Returns the total number of matches for the search    * (as opposed to just the number collected according    * to<code>offset()</code> and<code>size()</code>).    * Hence it's always true that matches()&gt;= size()    * @return number of matches for the search(query&amp; any filters)    */
DECL|method|matches
specifier|public
name|int
name|matches
parameter_list|()
function_decl|;
comment|/***   public int getDoc(int pos);   ***/
comment|// hmmm, what if a different slice could be generated from an existing DocSet
comment|// (and was before)...
comment|// how to distinguish cached values from logical values?
comment|// docSet could represent docs 10-20, but actually contain 0-100
comment|// should the big slice be cached independently, and a new class called
comment|// DocListSubset be created to refer to a range within the DocList?
comment|/**    * Get a subset of an existing DocList.    * Returns null if not possible.    */
DECL|method|subset
specifier|public
name|DocList
name|subset
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
function_decl|;
comment|/**    * Returns an iterator that may be used to iterate over the documents in this DocList    *    *<p>    * The order of the documents returned by this iterator is based on the    * Sort order of the search that produced it.  The Scoring information    * is meaningful only if<code>hasScores()</code> returns true.    *</p>    * @see #hasScores    */
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIterator
name|iterator
parameter_list|()
function_decl|;
comment|/** True if scores were retained */
DECL|method|hasScores
specifier|public
name|boolean
name|hasScores
parameter_list|()
function_decl|;
comment|/** The maximum score for the search... only valid if    * scores were retained (if hasScores()==true)    */
DECL|method|maxScore
specifier|public
name|float
name|maxScore
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|/****  Maybe do this at a higher level (more efficient)  class SmartDocSet implements DocSet {   static int INITIAL_SIZE=10;   static int TRANSITION_SIZE=10;    protected BitSet bits;   int size;    protected int[] arr;     // keep small set as an array, or as a hash?   protected int arrsize;    public SmartDocSet() {     if (INITIAL_SIZE>0) {       arr=new int[INITIAL_SIZE];     } else {       bits=new BitSet();     }   }     public void addUnique(int doc) {     size++;     if (bits != null) {       bits.set(doc);     }     else {       if (arrsize<10) {         arr[arrsize++]=doc;       } else  {         // TODO: transition to bit set       }     }   };    public int size() {     return size;   }   public boolean exists(int docid) {     return false;   }   public DocSet intersection(DocSet other) {     return null;    }   public DocSet union(DocSet other) {     return null;   } } ***/
end_comment

end_unit

