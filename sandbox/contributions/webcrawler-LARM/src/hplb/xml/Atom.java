begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * $Id$  *   * Copyright 1997 Hewlett-Packard Company  *   * This file may be copied, modified and distributed only in  * accordance with the terms of the limited licence contained  * in the accompanying file LICENSE.TXT.  */
end_comment

begin_package
DECL|package|hplb.xml
package|package
name|hplb
operator|.
name|xml
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_comment
comment|/**  * This class is responsible for maintaining strings as<em>atoms</em>,  * i.e. if two strings returned by getAtom() are equal in the sense of  * String.equal() then they are in fact the same Object. This is used to  * "intern" element and attribute names which can then be compared using  * the more efficient reference equality, a la "s1==s2".  *   * @author  Anders Kristensen  */
end_comment

begin_class
DECL|class|Atom
specifier|public
specifier|final
class|class
name|Atom
block|{
comment|/** Holds atoms: element names (GIs), and attribute names. */
DECL|field|atoms
specifier|private
specifier|static
specifier|final
name|Hashtable
name|atoms
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
comment|/**      * Return an atom corresponding to the argument.      */
DECL|method|getAtom
specifier|public
specifier|static
name|String
name|getAtom
parameter_list|(
name|String
name|s
parameter_list|)
block|{
synchronized|synchronized
init|(
name|atoms
init|)
block|{
name|String
name|a
init|=
operator|(
name|String
operator|)
name|atoms
operator|.
name|get
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|==
literal|null
condition|)
block|{
name|atoms
operator|.
name|put
argument_list|(
name|s
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|a
operator|=
name|s
expr_stmt|;
block|}
return|return
name|a
return|;
block|}
block|}
block|}
end_class

end_unit

