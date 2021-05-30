package com.hotechcourse.oauth.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @OneToOne
  @JoinColumn(name = "member_id")
  private User user;
  @Column(nullable = false, unique = true)
  private String token;
  private Date expiryDate;

  @Builder
  public RefreshToken(User user, String token, Date expiryDate) {
    this.user = user;
    this.token = token;
    this.expiryDate = expiryDate;
  }
}
