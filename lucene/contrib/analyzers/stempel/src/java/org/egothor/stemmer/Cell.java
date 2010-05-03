begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*                     Egothor Software License version 1.00                     Copyright (C) 1997-2004 Leo Galambos.                  Copyright (C) 2002-2004 "Egothor developers"                       on behalf of the Egothor Project.                              All rights reserved.     This  software  is  copyrighted  by  the "Egothor developers". If this    license applies to a single file or document, the "Egothor developers"    are the people or entities mentioned as copyright holders in that file    or  document.  If  this  license  applies  to the Egothor project as a    whole,  the  copyright holders are the people or entities mentioned in    the  file CREDITS. This file can be found in the same location as this    license in the distribution.     Redistribution  and  use  in  source and binary forms, with or without    modification, are permitted provided that the following conditions are    met:     1. Redistributions  of  source  code  must retain the above copyright        notice, the list of contributors, this list of conditions, and the        following disclaimer.     2. Redistributions  in binary form must reproduce the above copyright        notice, the list of contributors, this list of conditions, and the        disclaimer  that  follows  these  conditions  in the documentation        and/or other materials provided with the distribution.     3. The name "Egothor" must not be used to endorse or promote products        derived  from  this software without prior written permission. For        written permission, please contact Leo.G@seznam.cz     4. Products  derived  from this software may not be called "Egothor",        nor  may  "Egothor"  appear  in  their name, without prior written        permission from Leo.G@seznam.cz.     In addition, we request that you include in the end-user documentation    provided  with  the  redistribution  and/or  in the software itself an    acknowledgement equivalent to the following:    "This product includes software developed by the Egothor Project.     http://egothor.sf.net/"     THIS  SOFTWARE  IS  PROVIDED  ``AS  IS''  AND ANY EXPRESSED OR IMPLIED    WARRANTIES,  INCLUDING,  BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF    MERCHANTABILITY  AND  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.    IN  NO  EVENT  SHALL THE EGOTHOR PROJECT OR ITS CONTRIBUTORS BE LIABLE    FOR   ANY   DIRECT,   INDIRECT,  INCIDENTAL,  SPECIAL,  EXEMPLARY,  OR    CONSEQUENTIAL  DAMAGES  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF    SUBSTITUTE  GOODS  OR  SERVICES;  LOSS  OF  USE,  DATA, OR PROFITS; OR    BUSINESS  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,    WHETHER  IN  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE    OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN    IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.     This  software  consists  of  voluntary  contributions  made  by  many    individuals  on  behalf  of  the  Egothor  Project  and was originally    created by Leo Galambos (Leo.G@seznam.cz).  */
end_comment

begin_package
DECL|package|org.egothor.stemmer
package|package
name|org
operator|.
name|egothor
operator|.
name|stemmer
package|;
end_package

begin_comment
comment|/**  * A Cell is a portion of a trie.  */
end_comment

begin_class
DECL|class|Cell
class|class
name|Cell
block|{
comment|/** next row id in this way */
DECL|field|ref
name|int
name|ref
init|=
operator|-
literal|1
decl_stmt|;
comment|/** command of the cell */
DECL|field|cmd
name|int
name|cmd
init|=
operator|-
literal|1
decl_stmt|;
comment|/** how many cmd-s was in subtrie before pack() */
DECL|field|cnt
name|int
name|cnt
init|=
literal|0
decl_stmt|;
comment|/** how many chars would be discarded from input key in this way */
DECL|field|skip
name|int
name|skip
init|=
literal|0
decl_stmt|;
comment|/** Constructor for the Cell object. */
DECL|method|Cell
name|Cell
parameter_list|()
block|{}
comment|/**    * Construct a Cell using the properties of the given Cell.    *     * @param a the Cell whose properties will be used    */
DECL|method|Cell
name|Cell
parameter_list|(
name|Cell
name|a
parameter_list|)
block|{
name|ref
operator|=
name|a
operator|.
name|ref
expr_stmt|;
name|cmd
operator|=
name|a
operator|.
name|cmd
expr_stmt|;
name|cnt
operator|=
name|a
operator|.
name|cnt
expr_stmt|;
name|skip
operator|=
name|a
operator|.
name|skip
expr_stmt|;
block|}
comment|/**    * Return a String containing this Cell's attributes.    *     * @return a String representation of this Cell    */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ref("
operator|+
name|ref
operator|+
literal|")cmd("
operator|+
name|cmd
operator|+
literal|")cnt("
operator|+
name|cnt
operator|+
literal|")skp("
operator|+
name|skip
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

