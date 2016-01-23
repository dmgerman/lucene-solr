begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/** An "open" BitSet implementation that allows direct access to the array of words  * storing the bits.  *<p/>  * Unlike java.util.bitet, the fact that bits are packed into an array of longs  * is part of the interface.  This allows efficient implementation of other algorithms  * by someone other than the author.  It also allows one to efficiently implement  * alternate serialization or interchange formats.  *<p/>  *<code>OpenBitSet</code> is faster than<code>java.util.BitSet</code> in most operations  * and *much* faster at calculating cardinality of sets and results of set operations.  * It can also handle sets of larger cardinality (up to 64 * 2**32-1)  *<p/>  * The goals of<code>OpenBitSet</code> are the fastest implementation possible, and  * maximum code reuse.  Extra safety and encapsulation  * may always be built on top, but if that's built in, the cost can never be removed (and  * hence people re-implement their own version in order to get better performance).  * If you want a "safe", totally encapsulated (and slower and limited) BitSet  * class, use<code>java.util.BitSet</code>.  *<p/>  *<h3>Performance Results</h3>  *  Test system: Pentium 4, Sun Java 1.5_06 -server -Xbatch -Xmx64M<br/>BitSet size = 1,000,000<br/>Results are java.util.BitSet time divided by OpenBitSet time.<table border="1"><tr><th></th><th>cardinality</th><th>intersect_count</th><th>union</th><th>nextSetBit</th><th>get</th><th>iterator</th></tr><tr><th>50% full</th><td>3.36</td><td>3.96</td><td>1.44</td><td>1.46</td><td>1.99</td><td>1.58</td></tr><tr><th>1% full</th><td>3.31</td><td>3.90</td><td>&nbsp;</td><td>1.04</td><td>&nbsp;</td><td>0.99</td></tr></table><br/> Test system: AMD Opteron, 64 bit linux, Sun Java 1.5_06 -server -Xbatch -Xmx64M<br/>BitSet size = 1,000,000<br/>Results are java.util.BitSet time divided by OpenBitSet time.<table border="1"><tr><th></th><th>cardinality</th><th>intersect_count</th><th>union</th><th>nextSetBit</th><th>get</th><th>iterator</th></tr><tr><th>50% full</th><td>2.50</td><td>3.50</td><td>1.00</td><td>1.03</td><td>1.12</td><td>1.25</td></tr><tr><th>1% full</th><td>2.51</td><td>3.49</td><td>&nbsp;</td><td>1.00</td><td>&nbsp;</td><td>1.02</td></tr></table>   @deprecated Use {@link org.apache.lucene.util.OpenBitSet} directly.  * @version $Id$  */
end_comment

begin_class
DECL|class|OpenBitSet
specifier|public
class|class
name|OpenBitSet
extends|extends
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|OpenBitSet
implements|implements
name|Cloneable
implements|,
name|Serializable
block|{
comment|/** Constructs an OpenBitSet large enough to hold numBits.    *    * @param numBits    */
DECL|method|OpenBitSet
specifier|public
name|OpenBitSet
parameter_list|(
name|long
name|numBits
parameter_list|)
block|{
name|super
argument_list|(
name|numBits
argument_list|)
expr_stmt|;
block|}
DECL|method|OpenBitSet
specifier|public
name|OpenBitSet
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/** Constructs an OpenBitSet from an existing long[].    *<br/>    * The first 64 bits are in long[0],    * with bit index 0 at the least significant bit, and bit index 63 at the most significant.    * Given a bit index,    * the word containing it is long[index/64], and it is at bit number index%64 within that word.    *<p>    * numWords are the number of elements in the array that contain    * set bits (non-zero longs).    * numWords should be&lt= bits.length, and    * any existing words in the array at position&gt= numWords should be zero.    *    */
DECL|method|OpenBitSet
specifier|public
name|OpenBitSet
parameter_list|(
name|long
index|[]
name|bits
parameter_list|,
name|int
name|numWords
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

