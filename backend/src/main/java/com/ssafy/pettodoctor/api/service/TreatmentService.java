package com.ssafy.pettodoctor.api.service;

import com.ssafy.pettodoctor.api.domain.*;
import com.ssafy.pettodoctor.api.repository.*;
import com.ssafy.pettodoctor.api.repository.DoctorRepository;
import com.ssafy.pettodoctor.api.repository.HospitalRepository;
import com.ssafy.pettodoctor.api.repository.TreatmentRepositry;
import com.ssafy.pettodoctor.api.repository.UserRepository;
import com.ssafy.pettodoctor.api.request.NoticePostReq;
import com.ssafy.pettodoctor.api.request.PaymentReq;
import com.ssafy.pettodoctor.api.request.TreatmentPostReq;
import com.ssafy.pettodoctor.common.util.CancleRequestUtil;
import com.ssafy.pettodoctor.common.util.CheckTaskUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TreatmentService {
    private final TreatmentRepositry treatmentRepositry;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final NoticeRepository noticeRepository;
//    private final ExecutorServiceUtil executorServiceUtil;
    private final CheckTaskUtil checkTaskUtil;
    ExecutorService executorService = Executors.newCachedThreadPool();

    public Treatment findById(Long id){
        return treatmentRepositry.findByTreatmentId(id);
    }

    public Treatment findByPrescriptionId(Long id) { return treatmentRepositry.findByPrescriptionId(id); }

    public List<Treatment> findByDoctorIdAndType(Long id, TreatmentType treatmentType){
        return treatmentRepositry.findByDoctorIdAndType(id, treatmentType);
    }

    public List<Treatment> findByUserIdAndType(Long id, TreatmentType treatmentType){
        return treatmentRepositry.findByUserIdAndType(id, treatmentType);
    }

    public List<Treatment> findByDoctorId(Long id){
        return treatmentRepositry.findByDoctorId(id);
    }

    public List<Treatment> findByUserId(Long id){
        return treatmentRepositry.findByUserId(id);
    }

    @Transactional
    public Long registerTreatment(TreatmentPostReq treatmentPostReq) {
        Doctor doctor = doctorRepository.findById(treatmentPostReq.getDoctorId());
        User user = userRepository.findById(treatmentPostReq.getUserId()).get();
        Hospital hospital = hospitalRepository.findById(treatmentPostReq.getHospitalId());
        Long treatmentId = treatmentRepositry.registerTreatment(treatmentPostReq, doctor, user, hospital);
        Treatment treatment = treatmentRepositry.findByTreatmentId(treatmentId);
        String content = treatmentId + "??? [" + hospital.getName() + "-" + doctor.getName() + "] ";

        // ?????? ?????? ??????
//        Long user_id = treatmentPostReq.getUserId();
//        NoticePostReq noticeUserInfo = new NoticePostReq();
//        noticeUserInfo.setAccountId(user_id);
//        noticeUserInfo.setContent(content + "?????? ??????????????????. ????????? ????????? ?????????.");
//        noticeUserInfo.setUrl("https://"); // ?????? ?????????
//        noticeUserInfo.setType(NoticeType.PAYMENT);
//        noticeUserInfo.setIsChecked(false);
//        noticeUserInfo.setTreatmentId(treatmentId);
//        noticeUserInfo.setNoticeDate(LocalDateTime.now());
//        noticeRepository.registerNotice(noticeUserInfo, user, treatment);

        // ?????? ?????? ??????
        Notice notice = Notice.createNotice2(user, treatment, NoticeType.PAYMENT
                , content + "?????? ??????????????????. ????????? ????????? ?????????.");
        noticeRepository.save(notice);


        // ?????? ?????? ??????
        executorService.submit(checkTaskUtil.new CheckTask(treatmentId, TreatmentType.RES_REQUEST));

        return treatmentId;
    }

    // ?????? ??????
    @Transactional
    public Treatment updateTreatment(Long id, TreatmentType type){
        Long user_id = treatmentRepositry.findByTreatmentId(id).getUser().getId();

        NoticePostReq noticeUserInfo = new NoticePostReq();
        noticeUserInfo.setIsChecked(false);
        noticeUserInfo.setNoticeDate(LocalDateTime.now());
        noticeUserInfo.setAccountId(user_id);

        String hospitalName = treatmentRepositry.findByTreatmentId(id).getHospital().getName();
        String doctorName = treatmentRepositry.findByTreatmentId(id).getDoctor().getName();
        String content = id + "??? [" + hospitalName + "-" + doctorName + "] ";

//        if(type.equals(TreatmentType.RES_PAID) || type.equals(TreatmentType.VST_PAID)){ // ??????
//            noticeUserInfo.setContent(content + "????????? ?????????????????????.");
//            noticeUserInfo.setUrl("https://");
//            noticeRepository.registerNotice(noticeUserInfo, treatmentRepositry.findByTreatmentId(id).getUser(), null);
//
//            // ???????????? ??????
//            NoticePostReq noticeDoctorInfo = new NoticePostReq();
//            noticeDoctorInfo.setIsChecked(false);
//            noticeDoctorInfo.setNoticeDate(LocalDateTime.now());
//            noticeDoctorInfo.setAccountId(user_id);
//            noticeDoctorInfo.setContent(id + "??? - ????????? ????????? ????????? ????????????.");
//            noticeDoctorInfo.setUrl("https://");
//            noticeRepository.registerNotice(noticeDoctorInfo, treatmentRepositry.findByTreatmentId(id).getDoctor(),null);
//        }
//        else if(type.equals(TreatmentType.RES_CANCEL) || type.equals(TreatmentType.VST_CANCEL)){ // ?????? ??????
//            noticeUserInfo.setContent(content + "????????? ?????????????????????.");
//            noticeUserInfo.setUrl("https://");
//            noticeRepository.registerNotice(noticeUserInfo, treatmentRepositry.findByTreatmentId(id).getUser(), null);
//        }
//        else if(type.equals(TreatmentType.RES_REJECT) || type.equals(TreatmentType.VST_REJECT)){ // ?????? ??????
//            noticeUserInfo.setContent(content + "????????? ?????????????????????.");
//            noticeUserInfo.setUrl("https://");
//            noticeRepository.registerNotice(noticeUserInfo, treatmentRepositry.findByTreatmentId(id).getUser(), null);
//        }
//        else if(type.equals(TreatmentType.RES_ACCEPTED)){ // ?????? ??????
//            noticeRepository.updateNotice(noticeRepository.findBytreatmentId(id).getId(), NoticeType.RESERVATION );
//        }
//        else if(type.equals(TreatmentType.RES_ACCEPTED_CANCEL) || type.equals(TreatmentType.VST_ACCEPTED_CANCEL)){ // ???????????? ??????
//            noticeUserInfo.setContent(content + "??????????????? ?????????????????????.");
//            noticeUserInfo.setUrl("https://");
//            noticeRepository.registerNotice(noticeUserInfo, treatmentRepositry.findByTreatmentId(id).getUser(), null);
//        }

        return treatmentRepositry.updateTreatment(id, type);
    }

    // ?????? ??????
    // ?????? ??????
    @Transactional
    public Treatment updatePaymentInfo(Long treatmentId, PaymentReq paymentReq) throws Exception {

        Treatment treatment = treatmentRepositry.findByTreatmentId(treatmentId);

        if(!treatment.getType().equals(TreatmentType.RES_REQUEST)
        && !treatment.getType().equals(TreatmentType.VST_REQUEST))
            throw new Exception("????????? ?????? ?????????.");

        treatment.updatePaymentInfo(paymentReq.getPaymentCode(), paymentReq.getPrice());

        // ?????? ?????? ??????
        if(treatment.getType().equals(TreatmentType.RES_PAID))
            executorService.submit(checkTaskUtil.new CheckTask(treatmentId, TreatmentType.RES_PAID));
        else if(treatment.getType().equals(TreatmentType.VST_PAID))
            executorService.submit(checkTaskUtil.new CheckTask(treatmentId, TreatmentType.VST_PAID));

        // ??????????????? ?????? ??????
        Notice notice1 = Notice.createNotice2(treatment.getUser(), treatment, NoticeType.PAYMENT
                , treatment.getId() + "??? - ?????? ????????? ??????????????????.");
        noticeRepository.save(notice1);

        // ???????????? ?????? ??????
        Notice notice2 = Notice.createNotice2(treatment.getDoctor(), treatment, NoticeType.RESERVATION
                , treatment.getId() + "??? - ????????? ????????? ????????? ????????????.");
        noticeRepository.save(notice2);
        return treatment;
    }

    // ??????
    @Transactional
    public Treatment cancleTreatment(Long treatmentId, String reason) throws Exception{
        Treatment treatment = treatmentRepositry.findByTreatmentId(treatmentId);

        if(treatment.equals(TreatmentType.RES_COMPLETED)
                || treatment.equals(TreatmentType.VST_COMPLETED))
            throw new Exception("????????? ???????????????.");


        // ????????? ?????????????????? ??????
        if(treatment.getType().equals(TreatmentType.RES_PAID)
                || treatment.equals(TreatmentType.RES_CONFIRMED)
                || treatment.getType().equals(TreatmentType.VST_PAID)
                || treatment.equals(TreatmentType.VST_CONFIRMED)) {
            // access-token ??????
            String accessToken = CancleRequestUtil.getAccessToken2();
            // ?????? ?????? ?????? ?????????
            String mId = treatment.getPaymentCode();
            Integer amount = treatment.getPrice();
            CancleRequestUtil.passCancleRequest(accessToken, mId, reason, amount);

            // ?????? ?????? ??????
            Notice notice = Notice.createNotice2(treatment.getUser(), treatment, NoticeType.PAYMENT
                    , treatment.getId() + "??? - ????????? ?????? ???????????????.");
            noticeRepository.save(notice);
        }

        // ?????? Cancel ??? ??????
        if(treatment.equals(TreatmentType.RES_REQUEST)
                || treatment.equals(TreatmentType.RES_PAID)
                || treatment.equals(TreatmentType.RES_CONFIRMED))
            treatment.setType(TreatmentType.RES_CANCEL);
        else
            treatment.setType(TreatmentType.VST_CANCEL);

        // ?????? ?????? ??????
        Notice notice1 = Notice.createNotice2(treatment.getUser(), treatment, NoticeType.RESERVATION
                , treatment.getId() + "??? - ????????? ?????? ???????????????.");
        noticeRepository.save(notice1);
        // ???????????? ?????? ??????
        Notice notice2 = Notice.createNotice2(treatment.getDoctor(), treatment, NoticeType.RESERVATION
                , treatment.getId() + "??? - ????????? ?????? ???????????????.");
        noticeRepository.save(notice2);

        return treatment;
    }

    // confirm
    @Transactional
    public Treatment updateConfirm(Long treatmentId) throws Exception{

        Treatment treatment = treatmentRepositry.findByTreatmentId(treatmentId);

        if(!treatment.getType().equals(TreatmentType.RES_PAID)
                && !treatment.getType().equals(TreatmentType.VST_PAID))
            throw new Exception("????????? ???????????????.");

        if(treatment.getType().equals(TreatmentType.RES_PAID))
        {
            treatment.setType(TreatmentType.RES_CONFIRMED);
        } else{
            treatment.setType(TreatmentType.VST_CONFIRMED);
        }

        // ?????? ?????? ??????
        Notice notice1 = Notice.createNotice2(treatment.getUser(), treatment, NoticeType.RESERVATION
                , treatment.getId() + "??? - ????????? ?????? ???????????????.");
        noticeRepository.save(notice1);

        return treatment;
    }

    // complete
    @Transactional
    public Treatment updateComplete(Long treatmentId) throws Exception{

        Treatment treatment = treatmentRepositry.findByTreatmentId(treatmentId);

        if(!treatment.getType().equals(TreatmentType.RES_CONFIRMED)
                && !treatment.getType().equals(TreatmentType.VST_CONFIRMED))
            throw new Exception("????????? ???????????????.");

        if(treatment.getType().equals(TreatmentType.RES_CONFIRMED))
        {
            treatment.setType(TreatmentType.RES_COMPLETED);
        } else{
            treatment.setType(TreatmentType.VST_COMPLETED);
        }

        // ?????? ?????? ??????
        Notice notice1 = Notice.createNotice2(treatment.getUser(), treatment, NoticeType.RESERVATION
                , treatment.getId() + "??? - ????????? ?????? ???????????????.");
        noticeRepository.save(notice1);
        // ???????????? ?????? ??????
        Notice notice2 = Notice.createNotice2(treatment.getDoctor(), treatment, NoticeType.RESERVATION
                , treatment.getId() + "??? - ????????? ?????? ???????????????.");
        noticeRepository.save(notice2);

        return treatment;
    }
}
