package com.clinch.crm.authentication.services;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.clinch.crm.authentication.models.User;

/**
 * Spring Security's view of a {@link User}: the object the password check works with, and the
 * "principal" of a successful authentication.
 *
 * <p>Besides the standard fields it carries {@code id}, {@code email} and {@code isDelete}, which
 * {@link AuthServiceImpl} needs to build the token. Roles become {@link GrantedAuthority} objects
 * with the role name unchanged (no {@code ROLE_} prefix is added).
 */
public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 1L;

	/** Database id of the user. */
	private Long id;

	/** Login/display name (not the login key; that's {@link #email}). */
	private String username;

	/** Email, the login key. */
	private String email;

	/** {@code true} if the account is deactivated; checked by {@link AuthServiceImpl}. */
	private boolean isDelete;

	/** BCrypt hash, never serialized to JSON. */
	@JsonIgnore
	private String password;

	/** The user's roles as authorities. */
	private Collection<? extends GrantedAuthority> authorities;

	/**
	 * Creates the principal. Normally use {@link #build(User)} instead.
	 *
	 * @param id          database id
	 * @param username    login/display name
	 * @param email       email (login key)
	 * @param password    BCrypt hash
	 * @param isDelete    whether the account is deactivated
	 * @param authorities the user's roles
	 */
	public UserDetailsImpl(Long id, String username, String email, String password,boolean isDelete,
			Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.isDelete = isDelete ;
		this.authorities = authorities;
	}

	/**
	 * Converts a {@link User} entity, turning each role name into a {@link SimpleGrantedAuthority}.
	 *
	 * @param user the entity loaded from the database
	 * @return the principal for Spring Security
	 */
	public static UserDetailsImpl build(User user) {
		List<GrantedAuthority> authorities = user.getRoles().stream()
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		return new UserDetailsImpl(
				user.getId(),
				user.getUsername(),
				user.getEmail(),
				user.getPassword(),
				user.getIsDelete(),
				authorities);
	}

	/** @return the user's roles as authorities */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	/** @return the user's database id */
	public Long getId() {
		return id;
	}

	/** @return the user's email (login key) */
	public String getEmail() {
		return email;
	}



	/** @return {@code true} if the account is deactivated */
	public boolean getIsDelete() {
		return isDelete;
	}


	/** @return the BCrypt hash Spring compares the typed password against */
	@Override
	public String getPassword() {
		return password;
	}

	/** @return the login/display name (Spring's "username"; the login key is the email) */
	@Override
	public String getUsername() {
		return username;
	}

	/** @return always {@code true}: account expiry isn't modelled */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/** @return always {@code true}: account locking isn't modelled */
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	/** @return always {@code true}: password expiry isn't modelled */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/**
	 * Always {@code true}. Deactivation ({@code isDelete}) is checked separately in
	 * {@link AuthServiceImpl} after the password check, not through this flag.
	 *
	 * @return {@code true}
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * Two principals are equal if they have the same user id. ({@code hashCode} isn't overridden
	 * to match, so don't put these in hash-based collections.)
	 *
	 * @param o the other object
	 * @return {@code true} if {@code o} is a {@code UserDetailsImpl} with the same id
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}


}
