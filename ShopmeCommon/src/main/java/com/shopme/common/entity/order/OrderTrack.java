package com.shopme.common.entity.order;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.shopme.common.entity.IdBasedEntity;

@Entity
@Table(name = "order_track")
public class OrderTrack extends IdBasedEntity {

	@Column(length = 256)
	private String notes;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "order_status", length = 45, nullable = false)
	private OrderStatus orderStatus;
	
	private Date updatedTime;

	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	@Transient
	public String getUpdatedTimeOnForm() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		//  chuyển đổi date thành string
		return dateFormat.format(updatedTime);
	}
	
	public void setUpdatedTimeOnForm(String dateString) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		try {
			// phân tích string thành date
			updatedTime = dateFormat.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
}
